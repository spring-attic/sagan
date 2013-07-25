package org.springframework.site.domain.blog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.services.MarkdownService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BlogPostContentRendererTests {

	private BlogPostContentRenderer renderer;

	@Mock
	private MarkdownService markdownService;

	@Before
	public void setUp() throws Exception {
		renderer = new BlogPostContentRenderer(markdownService);
	}

	@Test
	public void sendsContentToMarkdownRenderer() throws Exception {
		given(markdownService.renderToHtml("CONTENT")).willReturn("RENDERED CONTENT");

		assertThat(renderer.render("CONTENT"), equalTo("RENDERED CONTENT"));
	}

	@Test
	public void rendersCallouts() throws Exception {
		given(markdownService.renderToHtml("CONTENT")).willReturn("[callout title=Title]Callout body[/callout]");

		assertThat(renderer.render("CONTENT"), equalTo("<div class=\"callout\">\n" +
				"<div class=\"callout-title\">Title</div>\n" +
				"Callout body\n" +
				"</div>"
		));
	}

	@Test
	public void rendersMultipleCallouts() throws Exception {
		given(markdownService.renderToHtml("CONTENT")).willReturn("[callout title=Title]Callout body[/callout] other content [callout title=Other Title]Other Callout body[/callout]");

		assertThat(renderer.render("CONTENT"), equalTo("<div class=\"callout\">\n" +
				"<div class=\"callout-title\">Title</div>\n" +
				"Callout body\n" +
				"</div>" + " other content " +
				"<div class=\"callout\">\n" +
				"<div class=\"callout-title\">Other Title</div>\n" +
				"Other Callout body\n" +
				"</div>"
		));
	}
}
