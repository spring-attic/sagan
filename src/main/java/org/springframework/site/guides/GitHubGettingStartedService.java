package org.springframework.site.guides;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class GitHubGettingStartedService implements GettingStartedService {

	private static final String URL = "https://api.github.com/repos/springframework-meta/gs-{guideId}/contents/README.src.md";

	private final GitHub gh;

	@Autowired
	public GitHubGettingStartedService(GitHub gh) {
		this.gh = gh;
	}

	@Override
	public String loadGuide(String guideId) {
		try {
			Map<String, String> readme = gh.restOperations().getForObject(URL, Map.class, guideId);
			return new String(Base64.decode(readme.get("content").getBytes()));
		}
		catch (RestClientException e) {
			throw new GuideNotFoundException(String.format("No getting started guide found for '%s'", guideId), e);
		}
	}

}
