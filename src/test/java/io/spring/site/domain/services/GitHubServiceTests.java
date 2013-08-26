package io.spring.site.domain.services;

import io.spring.site.domain.services.github.CachingGitHubRestClient;
import io.spring.site.domain.services.github.GitHubService;
import io.spring.site.test.FixtureLoader;

import org.apache.xerces.impl.dv.util.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.social.github.api.GitHubRepo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class GitHubServiceTests {

	@Mock
	CachingGitHubRestClient gitHubRestClient;
	private GitHubService service;

	@Before
	public void setUp() throws Exception {
		service = new GitHubService(gitHubRestClient);
	}

	@Test
	public void getRawFileAsHtml_fetchesRenderedHtmlFromGitHub() throws Exception {
		given(gitHubRestClient.sendRequestForHtml("/path/to/html")).willReturn("<h1>Something</h1>");

		assertThat(service.getRawFileAsHtml("/path/to/html"), equalTo("<h1>Something</h1>"));
	}

	@Test
	public void getRepoInfo_fetchesGitHubRepos() {
		String response = FixtureLoader.load("/fixtures/github/githubRepo.json");

		given(gitHubRestClient.sendRequestForJson(anyString(), eq("user"), eq("repo"))).willReturn(response);

		GitHubRepo repoInfo = service.getRepoInfo("user", "repo");
		assertThat(repoInfo.getName(), equalTo("spring-boot"));
	}

	@Test
	public void renderToHtml_sendsMarkdownToGithub_returnsHtml() {
		String response = "<h3>Title</h3>";

		given(gitHubRestClient.sendPostRequestForHtml(anyString(), eq("### Title"))).willReturn(response);

		assertThat(service.renderToHtml("### Title"), equalTo(response));
	}

	@Test
	public void getImage_fetchesImageFromGitHub() {
		byte[] expected = {1, 2, 3};
		String encoded = Base64.encode(expected);
		String response = String.format("{\"content\":\"%s\"}", encoded);

		given(gitHubRestClient.sendRequestForJson(anyString(), eq("my-repo"), eq("image.png"))).willReturn(response);

		assertThat(service.getGuideImage("my-repo", "image.png"), equalTo(expected));
	}

	@Test
	public void getGitHubRepos_fetchesGuideReposGitHub() {
		String response = FixtureLoader.load("/fixtures/github/githubRepoList.json");

		given(gitHubRestClient.sendRequestForJson(anyString())).willReturn(response);

		GitHubRepo[] repos = service.getGitHubRepos("/path/to/guide/repos");
		assertThat(repos[0].getName(), equalTo("gs-rest-service"));
	}

}
