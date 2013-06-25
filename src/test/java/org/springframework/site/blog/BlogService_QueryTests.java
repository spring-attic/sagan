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
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_QueryTests {
	private BlogService service;

	@Mock
	private PostRepository postRepository;

	@Mock
	private MarkdownService markdownService;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Before
	public void setup() {
		service = new BlogService(postRepository, markdownService);
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
		posts.add(new Post("title", "content", PostCategory.ENGINEERING));
		Page<Post> page = new PageImpl<Post>(posts);

		when(postRepository.findAll(firstTenPosts)).thenReturn(page);

		assertThat(service.mostRecentPosts(firstTenPosts), is(posts));
	}

	@Test
	public void listPostsForCategory() {
		Pageable firstTenPosts = new BlogPostsPageRequest(1);
		List<Post> posts = new ArrayList<Post>();
		posts.add(new Post("title", "content", PostCategory.ENGINEERING));
		Page<Post> page = new PageImpl<Post>(posts);

		when(postRepository.findByCategory(PostCategory.ENGINEERING, firstTenPosts)).thenReturn(page);

		assertThat(service.mostRecentPosts(PostCategory.ENGINEERING, firstTenPosts), is(posts));
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
