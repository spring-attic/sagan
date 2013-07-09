package org.springframework.site.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.guides.GuideHtml;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class GitHubService implements MarkdownService {

	public static final String HOSTNAME = "https://api.github.com";
	private GitHub gitHub;

	@Autowired
	public GitHubService(GitHub gitHub) {
		this.gitHub = gitHub;
	}

	@Override
	public String renderToHtml(String markdownSource) {
		return postForObject("/markdown/raw", markdownSource, String.class);
	}

	public <T> T postForObject(String path, Object request, Class<T> responseType, Object... uriVariables) {
		return gitHub.restOperations().postForObject(HOSTNAME + path, request, responseType, uriVariables);
	}

	public <T> T getForObject(String path, Class<T> responseType, Object... uriVariables) throws RestClientException {
		return gitHub.restOperations().getForObject(HOSTNAME + path, responseType, uriVariables);
	}

	public String getRawFileAsHtml(String path) {
		GuideHtml response = getForObject(path, GuideHtml.class);
		return response.getHtml();
	}
}