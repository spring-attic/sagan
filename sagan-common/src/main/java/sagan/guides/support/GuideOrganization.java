package sagan.guides.support;

import sagan.guides.DocumentContent;
import sagan.support.github.GitHubClient;
import sagan.support.github.Readme;
import sagan.support.github.RepoContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Repository representing the GitHub organization (or user account) that contains guide
 * repositories.
 */
@Component
class GuideOrganization {

    private static final String REPO_BASE_PATH = "/repos/{name}/{repo}";
    private static final String REPO_CONTENTS_PATH = REPO_BASE_PATH + "/contents";

    private final String type;
    private final GitHubClient gitHub;
    private final String name;
    private final ObjectMapper objectMapper;
    private final GuideRenderer renderer;

    @Autowired
    public GuideOrganization(@Value("${github.guides.owner.name}") String name,
                             @Value("${github.guides.owner.type}") String type,
                             GitHubClient gitHub, ObjectMapper objectMapper, GuideRenderer renderer) {
        this.name = name;
        this.type = type;
        this.gitHub = gitHub;
        this.objectMapper = objectMapper;
        this.renderer = renderer;
    }

    /**
     * The name of the GitHub organization or user that 'owns' guides.
     */
    public String getName() {
        return name;
    }

    public Readme getReadme(String path) {
        String json = gitHub.sendRequestForJson(path);

        try {
            return objectMapper.readValue(json, Readme.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AsciidocGuide getAsciidocGuide(String path) {
        DocumentContent guide = this.renderer.render(path);
        return new AsciidocGuide(guide.getContent(), guide.getTableOfContents());
    }

    public String getMarkdownFileAsHtml(String path) {
        return gitHub.sendRequestForHtml(path);
    }

    public GitHubRepo getRepoInfo(String repoName) {
        String json = gitHub.sendRequestForJson(REPO_BASE_PATH, name, repoName);

        try {
            return objectMapper.readValue(json, GitHubRepo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GitHubRepo[] findAllRepositories() {
        String json = gitHub.sendRequestForJson("/{type}/{name}/repos?per_page=100", type, name);

        try {
            return objectMapper.readValue(json, GitHubRepo[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<GitHubRepo> findRepositoriesByPrefix(String prefix) {
        List<GitHubRepo> matches = new ArrayList<>();
        for (GitHubRepo candidate : findAllRepositories()) {
            if (candidate.getName().startsWith(prefix)) {
                matches.add(candidate);
            }
        }
        return matches;
    }

    @SuppressWarnings("unchecked")
    public byte[] getGuideImage(String repoName, String imageName) {
        String json = gitHub.sendRequestForJson(REPO_CONTENTS_PATH + "/images/{image}", name, repoName, imageName);
        try {
            Map<String, String> map = objectMapper.readValue(json, Map.class);
            return Base64.decode(map.get("content").getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RepoContent> getRepoContents(String repoName) {
        String json = gitHub.sendRequestForJson(REPO_CONTENTS_PATH, name, repoName);
        try {
            return objectMapper.readValue(json, new TypeReference<List<RepoContent>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
