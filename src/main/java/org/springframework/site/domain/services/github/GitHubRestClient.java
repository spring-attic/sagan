package org.springframework.site.domain.services.github;

public interface GitHubRestClient {
	String sendRequestForJson(String path, Object... uriVariables);

	String sendRequestForHtml(String path, Object... uriVariables);

	String sendPostRequestForHtml(String path, String body, Object... uriVariables);
}
