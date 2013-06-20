package org.springframework.site.blog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.site.blog.repository.PostRepository;
import org.springframework.site.services.MarkdownService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_ValidPostTests {

	public static final String RENDERED_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p><p>from Markdown</p>";
	public static final String RENDERED_SUMMARY_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p>";
	private BlogService service;
	private Post post;
	private String title = "Title";
	private String content = "Rendered HTML\n\nfrom Markdown";
	private String firstParagraph = "Rendered HTML";

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
	public void postHasRenderedSummary() {
		assertThat(post.getRenderedSummary(), equalTo(RENDERED_SUMMARY_HTML_FROM_MARKDOWN));
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

	@Test
	public void listPosts() {
		Pageable firstTenPosts = new BlogPostsPageRequest(1);
		List<Post> posts = new ArrayList<Post>();
		posts.add(new Post("title", "content"));
		Page<Post> page = new PageImpl<Post>(posts);

		when(postRepository.findAll(firstTenPosts)).thenReturn(page);

		assertThat(service.mostRecentPosts(firstTenPosts), is(posts));
	}

	@Test(expected = BlogPostsNotFound.class)
	public void blogPostsNotFound() {
		ArrayList<Post> noPosts = new ArrayList<Post>();
		Page<Post> emptyPage = new PageImpl<Post>(noPosts);

		when(postRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

		service.mostRecentPosts(new BlogPostsPageRequest(1));
	}

	@Test
	public void givenOnePage_paginationInfoBasedOnCurrentPageAndTotalPosts() {
		when(postRepository.count()).thenReturn(1L);
		PaginationInfo paginationInfo = service.paginationInfo(new PageRequest(0, 10));
		assertThat(paginationInfo.getCurrentPage(), is(equalTo(1L)));
		assertThat(paginationInfo.getTotalPages(), is(equalTo(1L)));
	}

	@Test
	public void givenManyPages_paginationInfoBasedOnCurrentPageAndTotalPosts() {
		when(postRepository.count()).thenReturn(101L);
		PaginationInfo paginationInfo = service.paginationInfo(new PageRequest(0, 10));
		assertThat(paginationInfo.getCurrentPage(), is(equalTo(1L)));
		assertThat(paginationInfo.getTotalPages(), is(equalTo(11L)));
	}

	@Test
	public void extractFirstParagraph() {
		assertEquals("xx", service.extractFirstParagraph("xxxxx", 2));
		assertEquals("xx", service.extractFirstParagraph("xx\n\nxxx", 20));
		assertEquals("xx", service.extractFirstParagraph("xx xx\n\nxxx", 4));
	}

}
