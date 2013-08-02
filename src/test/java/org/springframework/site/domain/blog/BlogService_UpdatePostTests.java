package org.springframework.site.domain.blog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.site.test.DateTestUtils;

import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_UpdatePostTests {

	private BlogService service;
	private Post post;
	private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");
	private Date now = DateTestUtils.getDate("2013-07-01 13:00");

	@Mock
	private PostRepository postRepository;

	@Mock
	private DateService dateService;

	@Mock
	private SearchService searchService;

	@Mock
	private PostFormAdapter postFormAdapter;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	private PostForm postForm;


	@Before
	public void setup() {
		given(this.dateService.now()).willReturn(this.now);

		this.service = new BlogService(this.postRepository,
				this.postFormAdapter,
				this.dateService,
				this.searchService);

		this.post = PostBuilder.post()
				.id(123L)
				.publishAt(publishAt)
				.build();

		this.postForm = new PostForm(this.post);
		this.service.updatePost(this.post, this.postForm);
	}

	@Test
	public void postIsUpdated() {
		verify(postFormAdapter).updatePostFromPostForm(post, postForm);
	}

	@Test
	public void postIsPersisted() {
		verify(this.postRepository).save(this.post);
	}

	@Test
	public void updatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
		verify(this.searchService).saveToIndex((SearchEntry) anyObject());
	}

	@Test
	public void updatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
		reset(this.searchService);
		long postId = 123L;
		Post post = PostBuilder.post().id(postId).draft().build();
		this.service.updatePost(post, new PostForm(post));
		verifyZeroInteractions(this.searchService);
	}

}
