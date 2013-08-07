package org.springframework.site.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.site.domain.guides.GuideHtml;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class GitHubService implements MarkdownService {

	public static final String HOSTNAME = "https://api.github.com";
	private final CacheService cacheService;
	private GitHub gitHub;

	@Autowired
	public GitHubService(GitHub gitHub, CacheService cacheService) {
		this.gitHub = gitHub;
		this.cacheService = cacheService;
	}

	@Override
	public String renderToHtml(String markdownSource) {
		return postForObject("/markdown/raw", markdownSource, String.class);
	}

	public String getRawFileAsHtml(String path) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

		String etag = cacheService.getEtagForPath(path);
		if (etag != null) {
			headers.add("If-None-Match", etag);
		}

		ResponseEntity<GuideHtml> entity = gitHub.restOperations().exchange(HOSTNAME + path, HttpMethod.GET, new HttpEntity<>(headers), GuideHtml.class);

		if (entity.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
			return cacheService.getContentForPath(path);
		} else {
			cacheService.cacheContent(path, entity.getHeaders().getETag(), entity.getBody().getHtml());
			return entity.getBody().getHtml();
		}
	}

	public GitHubRepo getRepoInfo(String githubUsername, String repoName) {
		return gitHub.repoOperations().getRepo(githubUsername, repoName);
	}

	public GuideRepo[] getGuideRepos(String reposPath) {
		return getForObject(reposPath, GuideRepo[].class);
	}

	@SuppressWarnings("unchecked")
	public byte[] getImage(String imagesPath, String repoName, String imageName) {
		Map<String, String> response = getForObject(imagesPath, Map.class, repoName, imageName);

		return Base64.decode(response.get("content").getBytes());
	}

	private <T> T postForObject(String path, Object request, Class<T> responseType, Object... uriVariables) {
		return gitHub.restOperations().postForObject(HOSTNAME + path, request, responseType, uriVariables);
	}

	private <T> T getForObject(String path, Class<T> responseType, Object... uriVariables) throws RestClientException {
		return gitHub.restOperations().getForObject(HOSTNAME + path, responseType, uriVariables);
	}
}