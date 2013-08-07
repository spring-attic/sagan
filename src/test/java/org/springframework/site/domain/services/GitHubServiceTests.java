package org.springframework.site.domain.services;

import org.apache.xerces.impl.dv.util.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.domain.services.github.CachingGitHubRestClient;
import org.springframework.site.domain.services.github.GitHubService;
import org.springframework.site.test.FixtureLoader;
import org.springframework.social.github.api.GitHubRepo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GitHubServiceTests {

	@Mock
	CachingGitHubRestClient gitHubRestClient;
	private GitHubService service;

	@Before
	public void setUp() throws Exception {
		service = new GitHubService(gitHubRestClient, null);
	}

	@Test
	public void getRawFileAsHtml_fetchesRenderedHtmlFromGitHub() throws Exception {
		given(gitHubRestClient.sendRequestForHtml("/path/to/html")).willReturn("<h1>Something</h1>");

		assertThat(service.getRawFileAsHtml("/path/to/html"), equalTo("<h1>Something</h1>"));
	}

	@Test
	public void getRepoInfo_fetchesGitHubRepos() {
		String response = FixtureLoader.load("/fixtures/github/githubRepo.json");

		given(gitHubRestClient.sendRequestForJson("/repos/{user}/{repo}", "user", "repo")).willReturn(response);

		GitHubRepo repoInfo = service.getRepoInfo("user", "repo");
		assertThat(repoInfo.getName(), equalTo("spring-boot"));
	}

	@Test
	public void renderToHtml_sendsMarkdownToGithub_returnsHtml() {
		String response = "<h3>Title</h3>";

		given(gitHubRestClient.sendPostRequestForHtml("/markdown/raw", "### Title")).willReturn(response);

		assertThat(service.renderToHtml("### Title"), equalTo(response));
	}

	@Test
	public void getImage_fetchesImageFromGitHub() {
		byte[] expected = {1, 2, 3};
		String encoded = Base64.encode(expected);
		String response = String.format("{\"content\":\"%s\"}", encoded);

		given(gitHubRestClient.sendRequestForJson("/repos/springframework-meta/{guideId}/contents/images/{imageName}", "my-repo", "image.png")).willReturn(response);

		assertThat(service.getGuideImage("my-repo", "image.png"), equalTo(expected));
	}

	@Test
	public void getGuideRepos_fetchesGuideReposGitHub() {
		String response = FixtureLoader.load("/fixtures/github/githubRepoList.json");

		given(gitHubRestClient.sendRequestForJson("/path/to/guide/repos")).willReturn(response);

		GuideRepo[] repos = service.getGuideRepos("/path/to/guide/repos");
		assertThat(repos[0].getName(), equalTo("Spring-Integration-in-Action"));
	}

}
