package org.springframework.site.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Map;

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
		@SuppressWarnings("unchecked")
		Map<String, String> response = getForObject(path, Map.class);
		String raw = new String(extractCodedContent(response));
		return renderToHtml(raw);
	}

	private byte[] extractCodedContent(Map<String, String> jsonResponse) {
		return Base64.decode(jsonResponse.get("content").getBytes());
	}

}