package org.springframework.site.guides;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitHubGettingStartedService implements GettingStartedService {

	private static final String HOSTNAME = "https://api.github.com";
	private static final String REPOS_URL = HOSTNAME + "/orgs/springframework-meta/repos";
	private static final String README_URL = HOSTNAME + "/repos/springframework-meta/gs-{guideId}/contents/README.md";
	private static final String IMAGES_URL = HOSTNAME + "/repos/springframework-meta/gs-{guideId}/contents/images/{imageName}";

	private static final Logger log = Logger.getLogger(GitHubGettingStartedService.class);

	private final GitHub gh;

	@Autowired
	public GitHubGettingStartedService(GitHub gh) {
		this.gh = gh;
	}

	@Override
	public String loadGuide(String guideId) {
		try {
			log.info(String.format("Fetching getting started guide for '%s'", guideId));
			Map<String, String> readme = gh.restOperations().getForObject(README_URL, Map.class, guideId);
			String markdownReadme = new String(extractCodedContent(readme));
			return render(markdownReadme);
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

	private String render(String markdownReadme) {
		return gh.restOperations().postForObject(HOSTNAME + "/markdown/raw", markdownReadme, String.class);
	}

	@Override
	public List<Guide> listGuides() {
		List<Guide> guides = new ArrayList<Guide>();

		for (Guide guide : gh.restOperations().getForObject(REPOS_URL, Guide[].class)) {
			if (guide.isGettingStartedGuide()) {
				guides.add(guide);
			}
		}

		return guides;
	}

	@Override
	public byte[] loadImage(String guideSlug, String imageName) {
		try {
			Map<String, String> response = gh.restOperations().getForObject(IMAGES_URL, Map.class, guideSlug, imageName);
			return extractCodedContent(response);
		} catch (RestClientException e) {
			String msg = String.format("Could not load image '%s' for guide id '%s'", imageName, guideSlug);
			log.warn(msg, e);
			throw new ImageNotFoundException(msg, e);
		}
	}

}
