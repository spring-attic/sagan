package io.spring.site.domain.services.github;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.site.domain.services.MarkdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService implements MarkdownService {

	public static final String API_URL_BASE = "https://api.github.com";
	private final GitHubRestClient gitHubRestClient;
	private ObjectMapper objectMapper = new ObjectMapper();


	private static final String REPO_CONTENTS_PATH = "/repos/spring-guides/{repoId}/contents";
	private static final String GUIDE_IMAGES_PATH =  REPO_CONTENTS_PATH + "/images/{imageName}";


	@Autowired
	public GitHubService(GitHubRestClient gitHubRestClient) {
		this.gitHubRestClient = gitHubRestClient;
	}

	@Override
	public String renderToHtml(String markdownSource) {
		return gitHubRestClient.sendPostRequestForHtml("/markdown/raw", markdownSource);
	}

	public String getRawFileAsHtml(String path) {
		return gitHubRestClient.sendRequestForHtml(path);
	}

	public GitHubRepo getRepoInfo(String githubUsername, String repoName) {
		String response = gitHubRestClient.sendRequestForJson("/repos/{user}/{repo}", githubUsername, repoName);

		try {
			return objectMapper.readValue(response, GitHubRepo.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public GitHubRepo[] getGitHubRepos(String reposPath) {
		String response = gitHubRestClient.sendRequestForJson(reposPath);

		try {
			return objectMapper.readValue(response, GitHubRepo[].class);
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

	public List<RepoContent> getRepoContents(String repoId) {
		String jsonResponse = gitHubRestClient.sendRequestForJson(REPO_CONTENTS_PATH, repoId);
		try {
			return objectMapper.readValue(jsonResponse, new TypeReference<List<RepoContent>>(){});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getNameForUser(String username) {
		String jsonResponse = gitHubRestClient.sendRequestForJson("/users/{user}", username);
		try {
			Map<String, String> map = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>(){});
			return map.get("name");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}