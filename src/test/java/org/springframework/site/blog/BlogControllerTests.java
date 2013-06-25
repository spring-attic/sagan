package org.springframework.site.blog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BlogControllerTests {

	private static final int TEST_PAGE = 1;

	@Mock
	BlogService blogService;

	private BlogController controller;
	private ExtendedModelMap model = new ExtendedModelMap();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new BlogController(blogService);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void listPosts_providesAListOfPostsInTheModel(){
		List<Post> posts = new ArrayList<Post>();
		posts.add(PostBuilder.post().build());

		when(blogService.mostRecentPosts(any(Pageable.class))).thenReturn(posts);

		controller.listPosts(model, TEST_PAGE);

		assertThat((List<Post>) model.get("posts"), is(posts));
	}

	@Test
	public void listPosts_offsetsThePageToBeZeroIndexed(){
		controller.listPosts(model, TEST_PAGE);

		int zeroIndexedPage = TEST_PAGE - 1;
		BlogPostsPageRequest pageRequest = new BlogPostsPageRequest(zeroIndexedPage);
		verify(blogService).mostRecentPosts(eq(pageRequest));
	}

	@Test
	public void listPosts_providesPaginationInfo(){
		PaginationInfo paginationInfo = new PaginationInfo(TEST_PAGE, 20);

		when(blogService.paginationInfo(new BlogPostsPageRequest(TEST_PAGE-1))).thenReturn(paginationInfo);

		controller.listPosts(model, TEST_PAGE);

		assertThat((PaginationInfo) model.get("paginationInfo"), is(paginationInfo));
	}

	@Test
	public void listPostsView() {
		assertThat(controller.listPosts(model, TEST_PAGE), is("blog/index"));
	}

	@Test
	public void showPostModel() {
		Post post = PostBuilder.post().build();
		when(blogService.getPost(post.getId())).thenReturn(post);
		controller.showPost(post.getId(), "1-post-title", model);
		assertThat((Post) model.get("post"), is(post));
	}

	@Test
	public void showPostView() {
		assertThat(controller.showPost(1L, "not important", model), is("blog/show"));
	}

}
