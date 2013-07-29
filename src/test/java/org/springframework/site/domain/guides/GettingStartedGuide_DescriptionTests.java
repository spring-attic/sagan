package org.springframework.site.domain.guides;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.web.ViewRenderingHelper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GettingStartedGuide_DescriptionTests {

	private GettingStartedGuide guide;
	private ViewRenderingHelper helper;
	private GettingStartedGuide guideWithoutTitle;

	@Before
	public void setUp() throws Exception {
		guide = new GettingStartedGuide("gs-guide-id", "guide-id", "Title :: Subtitle", "Description", "Sidebar");
		guideWithoutTitle = new GettingStartedGuide("gs-guide-id", "guide-id", "Description", "Description", "Sidebar");
	}

	@Test
	public void testExtractTitleFromGithubRepoDescription() throws Exception {
		assertThat(guide.getTitle(), equalTo("Title"));
	}

	@Test
	public void testExtractTitleFromGithubRepoDescription_withoutTitle() throws Exception {
		assertThat(guideWithoutTitle.getTitle(), equalTo("Description"));
	}

	@Test
	public void testExtractSubtitleFromGithubRepoDescription() throws Exception {
		assertThat(guide.getSubtitle(), equalTo("Subtitle"));
	}

	@Test
	public void testExtractSubtitleFromGithubRepoDescription_withoutTitle() throws Exception {
		assertThat(guideWithoutTitle.getSubtitle(), equalTo(""));
	}
	
}
