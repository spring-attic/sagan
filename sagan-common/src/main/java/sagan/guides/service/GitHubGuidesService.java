package sagan.guides.service;

import sagan.guides.Guide;
import sagan.guides.GuideNotFoundException;
import sagan.guides.GuideWithoutContent;
import sagan.guides.ImageNotFoundException;
import sagan.util.service.github.GitHubService;
import sagan.util.service.github.Readme;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class GitHubGuidesService implements GuidesService {

    private static final String GITHUB_USERNAME = "spring-guides";
    private static final String REPOS_PATH = "/orgs/spring-guides/repos?per_page=100";
    private static final String README = "/repos/spring-guides/%s/readme";
    private static final String README_PATH_MD = "/repos/spring-guides/%s/contents/README.md";
    private static final String README_PATH_ASC = "/repos/spring-guides/%s/zipball";
    private static final String SIDEBAR_PATH = "/repos/spring-guides/%s/contents/SIDEBAR.md";
    private static final String TUTORIAL_PAGE_PATH = "/repos/spring-guides/%s/contents/%s/README.md";
    private static final Log log = LogFactory.getLog(GitHubGuidesService.class);
    private final GitHubService gitHubService;

    @Autowired
    public GitHubGuidesService(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @Override
    @Cacheable(value = "cache.guide", key = "#guideId")
    public Guide loadGettingStartedGuide(String guideId) {
        String repoName = getRepoNameFromGuideId(guideId);
        String description = getGuideDescription(repoName);

        Readme readme = getGuideReadme(String.format(README, repoName));

        String content = "";
        if (readme.getName().endsWith(".md")) {
            content = getMarkdownGuideContentAsHtml(String.format(README_PATH_MD, repoName));
        } else {
            content = getAsciiDocGuideContentAsHtml(String.format(README_PATH_ASC, repoName));
        }
        String sidebar = getGuideSidebar(repoName);
        String title = parseTitle(description);
        String subTitle = parseSubTitle(description);
        return new Guide(repoName, guideId, title, subTitle, content, sidebar);
    }

    private String parseTitle(String description) {
        String[] split = description.split("::", 2);
        return split[0].trim();
    }

    private String parseSubTitle(String description) {
        String[] split = description.split("::", 2);
        return (split.length > 1) ? split[1].trim() : "";
    }

    private String getRepoNameFromGuideId(String guideId) {
        return "gs-" + guideId;
    }

    private String getGuideDescription(String guideRepoName) {
        try {
            return this.gitHubService.getRepoInfo(GITHUB_USERNAME, guideRepoName).getDescription();
        } catch (RestClientException e) {
            return "";
        }
    }

    /**
     * Fetch the default readme file for this given guide repository, which may be written
     * in either Markdown or AsciiDoc.
     */
    private Readme getGuideReadme(String path) {
        try {
            log.debug(String.format("Fetching README for '%s'", path));
            return this.gitHubService.getReadme(path);
        } catch (RestClientException e) {
            String msg = String.format("No README found for '%s'", path);
            log.warn(msg, e);
            throw new GuideNotFoundException(msg, e);
        }
    }

    private String getMarkdownGuideContentAsHtml(String path) {
        try {
            log.debug(String.format("Fetching getting started guide for '%s'", path));
            return this.gitHubService.getMarkdownFileAsHtml(path);
        } catch (RestClientException e) {
            String msg = String.format("No getting started guide found for '%s'", path);
            log.warn(msg, e);
            throw new GuideNotFoundException(msg, e);
        }
    }

    private String getAsciiDocGuideContentAsHtml(String path) {
        try {
            log.debug(String.format("Fetching getting started guide for '%s'", path));
            return this.gitHubService.getAsciiDocFileAsHtml(path);
        } catch (RestClientException e) {
            String msg = String.format("No getting started guide found for '%s'", path);
            log.warn(msg, e);
            throw new GuideNotFoundException(msg, e);
        }
    }

    private String getGuideSidebar(String guideRepoName) {
        try {
            return this.gitHubService.getMarkdownFileAsHtml(String.format(SIDEBAR_PATH, guideRepoName));
        } catch (RestClientException e) {
            return "";
        }
    }

    @Override
    public List<GuideWithoutContent> listGettingStartedGuidesWithoutContent() {
        GitHubRepo[] guideRepos = this.gitHubService.getGitHubRepos(REPOS_PATH);
        return listGuidesWithoutContent(guideRepos, "gs-");
    }

    @Override
    public List<GuideWithoutContent> listTutorialsWithoutContent() {
        GitHubRepo[] guideRepos = this.gitHubService.getGitHubRepos(REPOS_PATH);
        return listGuidesWithoutContent(guideRepos, "tut-");
    }

    private List<GuideWithoutContent> listGuidesWithoutContent(GitHubRepo[] guideRepos, String prefix) {
        List<GuideWithoutContent> guides = new ArrayList<>();
        for (GitHubRepo repo : guideRepos) {
            String repoName = repo.getName();
            if (repoName.startsWith(prefix)) {
                String description = repo.getDescription();
                String title = parseTitle(description);
                String subTitle = parseSubTitle(description);
                guides.add(new GuideWithoutContent(repoName, repoName.replaceAll("^" + prefix, ""), title, subTitle));
            }
        }
        return guides;
    }

    @Override
    public List<Guide> listGettingStartedGuides() {
        GitHubRepo[] guideRepos = this.gitHubService.getGitHubRepos(REPOS_PATH);
        return mapGuideReposToGettingStartedGuides(guideRepos, "gs-");
    }

    @Override
    public List<Guide> listTutorials() {
        GitHubRepo[] guideRepos = this.gitHubService.getGitHubRepos(REPOS_PATH);
        return mapGuideReposToGettingStartedGuides(guideRepos, "tut-");
    }

    protected List<Guide> mapGuideReposToGettingStartedGuides(GitHubRepo[] guideRepos, String prefix) {
        List<Guide> guides = new ArrayList<>();
        for (GitHubRepo githubRepo : guideRepos) {
            String repoName = githubRepo.getName();
            if (repoName.startsWith(prefix)) {
                String description = githubRepo.getDescription();
                String title = parseTitle(description);
                String subTitle = parseSubTitle(description);
                guides.add(new Guide(repoName, repoName.replaceAll("^" + prefix, ""), title, subTitle,
                        getMarkdownGuideContentAsHtml(String.format(README_PATH_MD, repoName)),
                        getGuideSidebar(repoName)));
            }
        }
        return guides;
    }

    @Override
    public byte[] loadGettingStartedImage(String guideId, String imageName) {
        return loadImage(getRepoNameFromGuideId(guideId), imageName);
    }

    private byte[] loadImage(String repoName, String imageName) {
        try {
            return this.gitHubService.getGuideImage(repoName, imageName);
        } catch (RestClientException e) {
            String msg = String.format("Could not load image '%s' for repo '%s'", imageName, repoName);
            log.warn(msg, e);
            throw new ImageNotFoundException(msg, e);
        }
    }

    @Override
    public byte[] loadTutorialImage(String tutorialId, String imageName) {
        return loadImage("tut-" + tutorialId, imageName);
    }

    @Override
    public Guide loadTutorial(String tutorialId) {
        String repoName = "tut-" + tutorialId;
        String contentPath = String.format(README_PATH_MD, repoName);
        return loadTutorial(tutorialId, repoName, contentPath);
    }

    @Override
    public Guide loadTutorialPage(String tutorialId, String pagePath) {
        String repoName = "tut-" + tutorialId;
        String contentPath = String.format(TUTORIAL_PAGE_PATH, repoName, pagePath);
        return loadTutorial(tutorialId, repoName, contentPath);
    }

    private Guide loadTutorial(String tutorialId, String repoName, String contentPath) {
        String content = getMarkdownGuideContentAsHtml(contentPath);
        String description = getGuideDescription(repoName);
        String sidebar = getGuideSidebar(repoName);
        String title = parseTitle(description);
        String subTitle = parseSubTitle(description);
        return new Guide(repoName, tutorialId, title, subTitle, content, sidebar);
    }

}
