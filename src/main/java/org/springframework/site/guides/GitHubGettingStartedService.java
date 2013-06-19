package org.springframework.site.guides;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.site.services.GitHubService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitHubGettingStartedService implements GettingStartedService {

	private static final String REPOS_PATH = "/orgs/springframework-meta/repos";
	private static final String README_PATH = "/repos/springframework-meta/gs-{guideId}/contents/README.md";
	private static final String IMAGES_PATH = "/repos/springframework-meta/gs-{guideId}/contents/images/{imageName}";

	private static final Logger log = Logger.getLogger(GitHubGettingStartedService.class);

	private final GitHubService gitHubService;

	@Autowired
	public GitHubGettingStartedService(GitHubService gitHubService) {
		this.gitHubService = gitHubService;
	}

	@Override
	public String loadGuide(String guideId) {
		try {
			log.info(String.format("Fetching getting started guide for '%s'", guideId));
			@SuppressWarnings("unchecked")
			Map<String, String> readme = gitHubService.getForObject(README_PATH, Map.class, guideId);
			String markdownReadme = new String(extractCodedContent(readme));
			return gitHubService.renderToHtml(markdownReadme);
		}
		catch (RestClientException e) {
			String msg = String.format("No getting started guide found for '%s'", guideId);
			log.warn(msg, e);
			throw new GuideNotFoundException(msg, e);
		}
	}

	private byte[] extractCodedContent(Map<String, String> jsonResponse) {
		return Base64.decode(jsonResponse.get("content").getBytes());
	}

	@Override
	public List<Guide> listGuides() {
		List<Guide> guides = new ArrayList<Guide>();

		for (Guide guide : gitHubService.getForObject(REPOS_PATH, Guide[].class)) {
			if (guide.isGettingStartedGuide()) {
				guides.add(guide);
			}
		}

		return guides;
	}

	@Override
	public byte[] loadImage(String guideSlug, String imageName) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> response = gitHubService.getForObject(IMAGES_PATH, Map.class, guideSlug, imageName);
			return extractCodedContent(response);
		} catch (RestClientException e) {
			String msg = String.format("Could not load image '%s' for guide id '%s'", imageName, guideSlug);
			log.warn(msg, e);
			throw new ImageNotFoundException(msg, e);
		}
	}

}
