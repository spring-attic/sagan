package org.springframework.site.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.domain.guides.GettingStartedGuide;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ViewRenderingHelperTests {

	private GettingStartedGuide guide;
	private ViewRenderingHelper helper;
	private GettingStartedGuide guideWithoutTitle;

	@Before
	public void setUp() throws Exception {
		helper = new ViewRenderingHelper();
		guide = new GettingStartedGuide("gs-guide-id", "guide-id", "Title :: Subtitle", "Description", "Sidebar");
		guideWithoutTitle = new GettingStartedGuide("gs-guide-id", "guide-id", "Description", "Description", "Sidebar");
	}

	@Test
	public void testExtractTitleFromGithubRepoDescription() throws Exception {
		assertThat(helper.extractTitleFromRepoDescription(guide), equalTo("Title"));
	}

	@Test
	public void testExtractTitleFromGithubRepoDescription_withoutTitle() throws Exception {
		assertThat(helper.extractTitleFromRepoDescription(guideWithoutTitle), equalTo("Description"));
	}

	@Test
	public void testExtractSubtitleFromGithubRepoDescription() throws Exception {
		assertThat(helper.extractSubtitleFromRepoDescription(guide), equalTo("Subtitle"));
	}

	@Test
	public void testExtractSubtitleFromGithubRepoDescription_withoutTitle() throws Exception {
		assertThat(helper.extractSubtitleFromRepoDescription(guideWithoutTitle), equalTo(""));
	}
	
}
