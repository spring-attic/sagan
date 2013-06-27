package org.springframework.site.blog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.services.MarkdownService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_ValidPostTests {

	public static final String RENDERED_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p><p>from Markdown</p>";
	public static final String RENDERED_SUMMARY_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p>";
	private static final String AUTHOR = "author";
	private BlogService service;
	private Post post;
	private String title = "Title";
	private String content = "Rendered HTML\n\nfrom Markdown";
	private String firstParagraph = "Rendered HTML";
	private PostCategory category = PostCategory.ENGINEERING;
	private boolean broadcast = true;
	private boolean draft = false;

	@Mock
	private PostRepository postRepository;

	@Mock
	private MarkdownService markdownService;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Before
	public void setup() {
		service = new BlogService(postRepository, markdownService);
		when(markdownService.renderToHtml(content)).thenReturn(RENDERED_HTML_FROM_MARKDOWN);
		when(markdownService.renderToHtml(firstParagraph)).thenReturn(RENDERED_SUMMARY_HTML_FROM_MARKDOWN);
		PostForm postForm = new PostForm();
		postForm.setTitle(title);
		postForm.setContent(content);
		postForm.setCategory(category);
		postForm.setBroadcast(broadcast);
		post = service.addPost(postForm, AUTHOR);
	}

	@Test
	public void postHasCorrectUserEnteredValues() {
		assertThat(post.getTitle(), equalTo(title));
		assertThat(post.getRawContent(), equalTo(content));
		assertThat(post.getCategory(), equalTo(category));
		assertThat(post.isBroadcast(), equalTo(broadcast));
		assertThat(post.isDraft(), equalTo(draft));
	}

	@Test
	public void postHasAuthor() {
		assertThat(post.getAuthor(), equalTo(AUTHOR));
	}

	@Test
	public void postHasRenderedContent() {
		assertThat(post.getRenderedContent(), equalTo(RENDERED_HTML_FROM_MARKDOWN));
	}

	@Test
	public void postHasRenderedSummary() {
		assertThat(post.getRenderedSummary(), equalTo(RENDERED_SUMMARY_HTML_FROM_MARKDOWN));
	}

	@Test
	public void postIsPersisted() {
		verify(postRepository).save(any(Post.class));
	}

	@Test
	public void extractFirstParagraph() {
		assertEquals("xx", service.extractFirstParagraph("xxxxx", 2));
		assertEquals("xx", service.extractFirstParagraph("xx\n\nxxx", 20));
		assertEquals("xx", service.extractFirstParagraph("xx xx\n\nxxx", 4));
	}
}
