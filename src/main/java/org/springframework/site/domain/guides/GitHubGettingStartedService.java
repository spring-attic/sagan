package org.springframework.site.domain.guides;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.site.domain.services.GitHubService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitHubGettingStartedService implements GettingStartedService {

	private static final String GITHUB_USERNAME = "springframework-meta";
	private static final String REPOS_PATH = "/orgs/springframework-meta/repos?per_page=100";
	private static final String README_PATH = "/repos/springframework-meta/%s/contents/README.md";
	private static final String SIDEBAR_PATH = "/repos/springframework-meta/%s/contents/SIDEBAR.md";
	private static final String IMAGES_PATH = "/repos/springframework-meta/{guideId}/contents/images/{imageName}";

	private static final Log log = LogFactory.getLog(GitHubGettingStartedService.class);

	private final GitHubService gitHubService;

	@Autowired
	public GitHubGettingStartedService(GitHubService gitHubService) {
		this.gitHubService = gitHubService;
	}

	@Override
	public GettingStartedGuide loadGuide(String guideId) {
		String guideRepoName = getRepoNameFromGuideId(guideId);
		return new GettingStartedGuide(guideRepoName, guideId, getGuideDescription(guideRepoName), getGuideContent(guideRepoName), getGuideSidebar(guideRepoName));
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

	private String getGuideContent(String guideRepoName) {
		try {
			log.info(String.format("Fetching getting started guide for '%s'", guideRepoName));
			return this.gitHubService.getRawFileAsHtml(String.format(README_PATH, guideRepoName));
		} catch (RestClientException e) {
			String msg = String
					.format("No getting started guide found for '%s'", guideRepoName);
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
	public List<GettingStartedGuide> listGuides() {
		GuideRepo[] guideRepos = this.gitHubService.getForObject(REPOS_PATH, GuideRepo[].class);
		return mapGuideReposToGettingStartedGuides(guideRepos);
	}

	private List<GettingStartedGuide> mapGuideReposToGettingStartedGuides(GuideRepo[] guideRepos) {
		List<GettingStartedGuide> guides = new ArrayList<>();
		for (GuideRepo githubRepo : guideRepos) {
			if (githubRepo.isGettingStartedGuide()) {
				guides.add(new GettingStartedGuide(githubRepo.getName(), githubRepo.getGuideId(), githubRepo.getDescription(), null, null));
			}
		}
		return guides;
	}

	@Override
	public byte[] loadImage(String guideId, String imageName) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> response = this.gitHubService.getForObject(IMAGES_PATH,
					Map.class, getRepoNameFromGuideId(guideId), imageName);
			return Base64.decode(response.get("content").getBytes());
		} catch (RestClientException e) {
			String msg = String.format("Could not load image '%s' for guide id '%s'", imageName, guideId);
			log.warn(msg, e);
			throw new ImageNotFoundException(msg, e);
		}
	}

}
