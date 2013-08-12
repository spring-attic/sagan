package org.springframework.site.domain.guides;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.services.github.GitHubService;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubGuidesService implements GuidesService {

	private static final String GITHUB_USERNAME = "springframework-meta";
	private static final String REPOS_PATH = "/orgs/springframework-meta/repos?per_page=100";
	private static final String README_PATH = "/repos/springframework-meta/%s/contents/README.md";
	private static final String SIDEBAR_PATH = "/repos/springframework-meta/%s/contents/SIDEBAR.md";

	private static final Log log = LogFactory.getLog(GitHubGuidesService.class);

	private final GitHubService gitHubService;
	private static final String TUTORIAL_PAGE_PATH = "/repos/springframework-meta/%s/contents/%s/README.md";

	@Autowired
	public GitHubGuidesService(GitHubService gitHubService) {
		this.gitHubService = gitHubService;
	}

	@Override
	public Guide loadGettingStartedGuide(String guideId) {
		String repoName = getRepoNameFromGuideId(guideId);
		String description = getGuideDescription(repoName);
		String content = getGuideContent(String.format(README_PATH, repoName));
		String sidebar = getGuideSidebar(repoName);
		return new Guide(repoName, guideId, description, content, sidebar);
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

	private String getGuideContent(String path) {
		try {
			log.info(String.format("Fetching getting started guide for '%s'", path));
			return this.gitHubService.getRawFileAsHtml(path);
		} catch (RestClientException e) {
			String msg = String
					.format("No getting started guide found for '%s'", path);
			log.warn(msg, e);
			throw new GuideNotFoundException(msg, e);
		}
	}

	private String getGuideSidebar(String guideRepoName) {
		try {
			return this.gitHubService.getRawFileAsHtml(String.format(SIDEBAR_PATH, guideRepoName));
		} catch (RestClientException e) {
			return "";
		}
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

	private List<Guide> mapGuideReposToGettingStartedGuides(GitHubRepo[] guideRepos, String prefix) {
		List<Guide> guides = new ArrayList<>();
		for (GitHubRepo githubRepo : guideRepos) {
			if (githubRepo.getName().startsWith(prefix)) {
				guides.add(new Guide(githubRepo.getName(), githubRepo.getName().replaceAll("^"+prefix, ""), githubRepo.getDescription(), null, null));
			}
		}
		return guides;
	}

	@Override
	public byte[] loadImage(String guideId, String imageName) {
		try {
			return this.gitHubService.getGuideImage(getRepoNameFromGuideId(guideId), imageName);
		} catch (RestClientException e) {
			String msg = String.format("Could not load image '%s' for guide id '%s'", imageName, guideId);
			log.warn(msg, e);
			throw new ImageNotFoundException(msg, e);
		}
	}


	@Override
	public Guide loadTutorial(String tutorialId) {
		String repoName = "tut-" + tutorialId;
		return new Guide(repoName, tutorialId, getGuideDescription(repoName), getGuideContent(String.format(README_PATH, repoName)), "");
	}

	@Override
	public Guide loadTutorialPage(String tutorialId, String pagePath) {
		String repoName = "tut-" + tutorialId;
		String tutorialPagePath = String.format(TUTORIAL_PAGE_PATH, repoName, pagePath);

		return new Guide(repoName, tutorialId, getGuideDescription(repoName), getGuideContent(tutorialPagePath), "");
	}


}
