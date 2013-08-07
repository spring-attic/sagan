package org.springframework.site.domain.services.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.domain.services.MarkdownService;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Map;

@Service
public class GitHubService implements MarkdownService {

	public static final String API_URL_BASE = "https://api.github.com";
	private final CachingGitHubRestClient gitHubRestClient;
	private final GitHub gitHub;
	private ObjectMapper objectMapper = new ObjectMapper();

	private static final String GUIDE_IMAGES_PATH = "/repos/springframework-meta/{guideId}/contents/images/{imageName}";


	@Autowired
	public GitHubService(CachingGitHubRestClient cachingGitHubRestClient, GitHub gitHub) {
		this.gitHubRestClient = cachingGitHubRestClient;
		this.gitHub = gitHub;
	}

	@Override
	public String renderToHtml(String markdownSource) {
		return gitHubRestClient.sendPostRequestForHtml("/markdown/raw", markdownSource);
	}

	public String getRawFileAsHtml(String path) {
		return gitHubRestClient.sendRequestForHtml(path);
	}

	public GitHubRepo getRepoInfo(String githubUsername, String repoName) {
		String response = gitHubRestClient.sendRequestForJson(buildRepoUri(""), githubUsername, repoName);

		try {
			return objectMapper.readValue(response, GitHubRepo.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public GuideRepo[] getGuideRepos(String reposPath) {
		String response = gitHubRestClient.sendRequestForJson(reposPath);

		try {
			return objectMapper.readValue(response, GuideRepo[].class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public byte[] getGuideImage(String repoName, String imageName) {
		String response = gitHubRestClient.sendRequestForJson(GUIDE_IMAGES_PATH, repoName, imageName);
		try {
			Map<String, String> json = objectMapper.readValue(response, Map.class);
			return Base64.decode(json.get("content").getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private <T> T postForObject(String path, Object request, Class<T> responseType, Object... uriVariables) {
		return gitHub.restOperations().postForObject(API_URL_BASE + path, request, responseType, uriVariables);
	}

	private <T> T getForObject(String path, Class<T> responseType, Object... uriVariables) throws RestClientException {
		return gitHub.restOperations().getForObject(API_URL_BASE + path, responseType, uriVariables);
	}

	private String buildRepoUri(String path) {
		return "/repos/{user}/{repo}" + path;
	}

}