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

	private static final String REPOS_PATH = "/orgs/springframework-meta/repos?per_page=100";
	private static final String README_PATH = "/repos/springframework-meta/gs-%s/contents/README.md";
	private static final String SIDEBAR_PATH = "/repos/springframework-meta/gs-%s/contents/SIDEBAR.md";
	private static final String IMAGES_PATH = "/repos/springframework-meta/gs-{guideId}/contents/images/{imageName}";

	private static final Log log = LogFactory.getLog(GitHubGettingStartedService.class);

	private final GitHubService gitHubService;

	@Autowired
	public GitHubGettingStartedService(GitHubService gitHubService) {
		this.gitHubService = gitHubService;
	}

	@Override
	public GettingStartedGuide loadGuide(String guideId) {
		return new GettingStartedGuide(guideId, getGuideContent(guideId), getGuideSidebar(guideId));
	}

	private String getGuideContent(String guideId) {
		try {
			log.info(String.format("Fetching getting started guide for '%s'", guideId));
			return this.gitHubService.getRawFileAsHtml(String.format(README_PATH, guideId));
		} catch (RestClientException e) {
			String msg = String
					.format("No getting started guide found for '%s'", guideId);
			log.warn(msg, e);
			throw new GuideNotFoundException(msg, e);
		}
	}

	private String getGuideSidebar(String guideId) {
		try {
			return this.gitHubService.getRawFileAsHtml(String.format(SIDEBAR_PATH,
					guideId));
		} catch (RestClientException e) {
			return "";
		}
	}

	@Override
	public List<GuideRepo> listGuides() {
		List<GuideRepo> guideRepos = new ArrayList<GuideRepo>();

		for (GuideRepo guideRepo : this.gitHubService.getForObject(REPOS_PATH,
				GuideRepo[].class)) {
			if (guideRepo.isGettingStartedGuide()) {
				guideRepos.add(guideRepo);
			}
		}

		return guideRepos;
	}

	@Override
	public byte[] loadImage(String guideSlug, String imageName) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> response = this.gitHubService.getForObject(IMAGES_PATH,
					Map.class, guideSlug, imageName);
			return Base64.decode(response.get("content").getBytes());
		} catch (RestClientException e) {
			String msg = String.format("Could not load image '%s' for guide id '%s'",
					imageName, guideSlug);
			log.warn(msg, e);
			throw new ImageNotFoundException(msg, e);
		}
	}

}
