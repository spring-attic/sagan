package sagan.guides.support;

import org.asciidoctor.Attributes;
import sagan.util.service.github.GitHubClient;
import sagan.util.service.github.Readme;
import sagan.util.service.github.RepoContent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Repository representing the GitHub organization for guide repositories.
 * 
 * @author Chris Beams
 */
@Component
class GuideOrganization {

    private static final String REPO_BASE_PATH = "/repos/{org}/{repo}";
    private static final String REPO_CONTENTS_PATH = REPO_BASE_PATH + "/contents";

    private final GitHubClient gitHub;
    private final String name;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public GuideOrganization(@Value("${github.org.name:spring-guides}") String name, GitHubClient gitHub) {
        this.name = name;
        this.gitHub = gitHub;
    }

    /**
     * The name of this GitHub organization.
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

    public String getAsciiDocFileAsHtml(String path) {
        String content = null;

        byte[] download = gitHub.sendRequestForDownload(path);

        String tempFilePrefix = path.replace("/", "-");
        try {
            // First, write the downloaded stream of bytes into a file
            File zipball = File.createTempFile(tempFilePrefix, ".zip");
            zipball.deleteOnExit();
            FileOutputStream zipOut = new FileOutputStream(zipball);
            zipOut.write(download);
            zipOut.close();

            // Open the zip file and unpack it
            ZipFile zipFile = new ZipFile(zipball);
            File unzippedRoot = null;
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry entry = e.nextElement();
                if (entry.isDirectory()) {
                    File dir = new File(zipball.getParent() + File.separator + entry.getName());
                    dir.mkdir();
                    if (unzippedRoot == null) {
                        unzippedRoot = dir; // first directory is the root
                    }
                } else {
                    StreamUtils.copy(zipFile.getInputStream(entry),
                            new FileOutputStream(zipball.getParent() + File.separator + entry.getName()));
                }
            }

            // Process the unzipped guide through asciidoctor, rendering HTML content
            Asciidoctor asciidoctor = Asciidoctor.Factory.create();
            Attributes attributes = new Attributes();
            attributes.setAllowUriRead(true);
            content = asciidoctor.renderFile(
                    new File(unzippedRoot.getAbsolutePath() + File.separator + "README.adoc"),
                    OptionsBuilder.options().safe(SafeMode.SAFE).attributes(attributes));

            // Delete the zipball and the unpacked content
            FileSystemUtils.deleteRecursively(zipball);
            FileSystemUtils.deleteRecursively(unzippedRoot);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create temp file for source: " + tempFilePrefix);
        }

        return content;
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
        String json = gitHub.sendRequestForJson("/orgs/{org}/repos?per_page=100", name);

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
