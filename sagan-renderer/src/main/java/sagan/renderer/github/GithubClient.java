package sagan.renderer.github;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sagan.renderer.RendererProperties;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Client for the Github developer API
 */
@Component
public class GithubClient {

	public static final String API_URL_BASE = "https://api.github.com";

	private static final Pattern NEXT_LINK_PATTERN = Pattern.compile(".*<([^>]*)>;\\s*rel=\"next\".*");

	private static final Logger logger = LoggerFactory.getLogger(GithubClient.class);

	private static final String REPOS_LIST_PATH = "/orgs/%s/repos?per_page=100";

	private static final String REPO_INFO_PATH = "/repos/{organization}/{repositoryName}";

	private static final String REPO_ZIPBALL_PATH = REPO_INFO_PATH + "/zipball";

	private static final MediaType GITHUB_PREVIEW_TYPE = MediaType.parseMediaType("application/vnd.github.mercy-preview+json");

	private final RestTemplate restTemplate;

	public GithubClient(RestTemplateBuilder restTemplateBuilder,
			RendererProperties properties) {
		restTemplateBuilder = restTemplateBuilder
				.rootUri(API_URL_BASE)
				.additionalInterceptors(new GithubAcceptInterceptor());
		if (StringUtils.hasText(properties.getGithub().getToken())) {
			this.restTemplate = restTemplateBuilder
					.additionalInterceptors(new GithubAppTokenInterceptor(properties.getGithub().getToken()))
					.build();
		}
		else {
			this.logger.warn("GitHub API access will be rate-limited at 60 req/hour");
			this.restTemplate = restTemplateBuilder.build();
		}
	}

	/**
	 * Download a repository as a zipball
	 * @param organization the github organization name
	 * @param repository the repository name
	 * @return the zipball as raw bytes
	 */
	public byte[] downloadRepositoryAsZipball(String organization, String repository) {
		try {
			byte[] response = this.restTemplate.getForObject(REPO_ZIPBALL_PATH,
					byte[].class, organization, repository);
			return response;
		}
		catch (HttpClientErrorException ex) {
			throw new GithubResourceNotFoundException(organization, ex);
		}
	}

	/**
	 * Lists all the repositories available under the given organization
	 * @param organization the github organization name
	 * @return the list of all repositories under that organization
	 */
	public List<Repository> fetchOrgRepositories(String organization) {
		List<Repository> repositories = new ArrayList<>();
		Optional<String> nextPage = Optional.of(String.format(REPOS_LIST_PATH, organization));
		while (nextPage.isPresent()) {
			ResponseEntity<Repository[]> page = this.restTemplate
					.getForEntity(nextPage.get(), Repository[].class, organization);
			repositories.addAll(Arrays.asList(page.getBody()));
			nextPage = findNextPageLink(page);
		}
		return repositories;
	}

	/**
	 * Fetch repository information under the given organization
	 * @param organization the github organization name
	 * @param repositoryName the github repository name
	 * @return the repository information
	 */
	public Repository fetchOrgRepository(String organization, String repositoryName) {
		try {
			return this.restTemplate
					.getForObject(REPO_INFO_PATH, Repository.class, organization, repositoryName);
		}
		catch (HttpClientErrorException ex) {
			throw new GithubResourceNotFoundException(organization, repositoryName, ex);
		}
	}

	private Optional<String> findNextPageLink(ResponseEntity response) {
		List<String> links = response.getHeaders().get("Link");
		if (links == null) {
			return Optional.empty();
		}
		return links.stream()
				.map(NEXT_LINK_PATTERN::matcher)
				.filter(Matcher::matches)
				.map(matcher -> matcher.group(1))
				.findFirst();
	}

	private static class GithubAppTokenInterceptor implements ClientHttpRequestInterceptor {

		private final String token;

		GithubAppTokenInterceptor(String token) {
			this.token = token;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body,
				ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
			if (StringUtils.hasText(this.token)) {
				httpRequest.getHeaders().set(HttpHeaders.AUTHORIZATION,
						"Token " + this.token);
			}
			return clientHttpRequestExecution.execute(httpRequest, body);
		}

	}

	private static class GithubAcceptInterceptor implements ClientHttpRequestInterceptor {

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body,
				ClientHttpRequestExecution execution) throws IOException {
			request.getHeaders().setAccept(Collections.singletonList(GITHUB_PREVIEW_TYPE));
			return execution.execute(request, body);
		}
	}

}