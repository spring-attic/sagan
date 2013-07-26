package org.springframework.site.web;

import org.junit.Test;
import org.springframework.social.github.api.GitHubRepo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ViewRenderingHelperTests {

	@Test
	public void testExtractTitleFromGithubRepoDescription() throws Exception {
		ViewRenderingHelper helper = new ViewRenderingHelper();
		GitHubRepo repo = new GitHubRepo();
		repo.setDescription("Title :: Subtitle");
		assertThat(helper.extractTitleFromRepoDescription(repo), equalTo("Title"));
	}

	@Test
	public void testExtractTitleFromGithubRepoDescription_withoutTitle() throws Exception {
		ViewRenderingHelper helper = new ViewRenderingHelper();
		GitHubRepo repo = new GitHubRepo();
		repo.setDescription("Description");
		assertThat(helper.extractTitleFromRepoDescription(repo), equalTo("Description"));
	}

	@Test
	public void testExtractSubtitleFromGithubRepoDescription() throws Exception {
		ViewRenderingHelper helper = new ViewRenderingHelper();
		GitHubRepo repo = new GitHubRepo();
		repo.setDescription("Title :: Subtitle");
		assertThat(helper.extractSubtitleFromRepoDescription(repo), equalTo("Subtitle"));
	}

	@Test
	public void testExtractSubtitleFromGithubRepoDescription_withoutTitle() throws Exception {
		ViewRenderingHelper helper = new ViewRenderingHelper();
		GitHubRepo repo = new GitHubRepo();
		repo.setDescription("Description");
		assertThat(helper.extractSubtitleFromRepoDescription(repo), equalTo(""));
	}
	
}
