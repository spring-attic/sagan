package org.springframework.site.blog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.NoSuchBlogPostException;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.repository.PostRepository;
import org.springframework.site.services.MarkdownService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_ValidPostTests {

	public static final String RENDERED_HTML_FROM_MARKDOWN = "Rendered HTML from Markdown";
	private BlogService service;
	private Post post;
	private String title = "Title";
	private String content = "#Subtitle\nBody";

	@Mock
	private PostRepository postRepository;

	@Mock
	private MarkdownService markdownService;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Before
	public void setup() {
		service = new BlogService(postRepository, markdownService);
		when(markdownService.renderToHtml(anyString())).thenReturn(RENDERED_HTML_FROM_MARKDOWN);
		post = service.addPost(title, content);
	}

	@Test
	public void postHasCorrectUserEnteredValues() {
		assertThat(post.getTitle(), equalTo(title));
		assertThat(post.getRawContent(), equalTo(content));
	}

	@Test
	public void postHasRenderedContent() {
		assertThat(post.getRenderedContent(), equalTo(RENDERED_HTML_FROM_MARKDOWN));
	}

	@Test
	public void postIsPersisted() {
		verify(postRepository).save(any(Post.class));
	}

	@Test
	public void postIsRetrievable() {
		when(postRepository.findOne(anyLong())).thenReturn(post);
		assertThat(post, equalTo(service.getPost(post.getId())));
		verify(postRepository).findOne(anyLong());
	}

	@Test
	public void nonExistentPost() {
		when(postRepository.findOne(anyLong())).thenReturn(null);
		expected.expect(NoSuchBlogPostException.class);
		service.getPost(999L);
	}

}
