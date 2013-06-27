package org.springframework.site.blog.feed;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import org.junit.Before;
import org.junit.Test;
import org.springframework.site.services.SiteUrl;
import org.springframework.ui.ExtendedModelMap;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlogPostAtomViewerTests {

	private ExtendedModelMap model = new ExtendedModelMap();
	private SiteUrl siteUrl;
	private BlogPostAtomViewer blogPostAtomViewer;
	private Feed feed = new Feed();

	@Before
	public void setUp() throws Exception {
		siteUrl = mock(SiteUrl.class);
		blogPostAtomViewer = new BlogPostAtomViewer(siteUrl);
	}

	@Test
	public void hasFeedTitleFromModel() {
		model.addAttribute("feed-title", "Spring Engineering");
		blogPostAtomViewer.buildFeedMetadata(model, feed, mock(HttpServletRequest.class));
		assertThat(feed.getTitle(), is("Spring Engineering"));
	}

	@Test
	public void hasCategoryInLink() {
		String expectedFeedPath = "/blog/category/engineering.atom";
		String expectedFeedUrl = "http://localhost:8080/blog/category/engineering.atom";
		when(siteUrl.getAbsoluteUrl(eq(expectedFeedPath))).thenReturn(expectedFeedUrl);
		model.addAttribute("feed-path", expectedFeedPath);

		blogPostAtomViewer.buildFeedMetadata(model, feed, mock(HttpServletRequest.class));

		Link feedLink = (Link) feed.getAlternateLinks().get(0);
		assertThat(feedLink.getHref(), is(expectedFeedUrl));
	}
}
