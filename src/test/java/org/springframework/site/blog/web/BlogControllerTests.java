package org.springframework.site.blog.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.site.blog.*;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.site.blog.PostCategory.*;

public class BlogControllerTests {

	private static final int TEST_PAGE = 1;

	@Mock
    BlogService blogService;

	private BlogController controller;
	private ExtendedModelMap model = new ExtendedModelMap();
	private final List<Post> posts = new ArrayList<Post>();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new BlogController(blogService);
		posts.add(PostBuilder.post().build());
		when(blogService.getPublishedPosts(any(Pageable.class))).thenReturn(posts);
		when(blogService.getPublishedBroadcastPosts(any(Pageable.class))).thenReturn(posts);
	}

	private void assertThatPostsAreInModel() {
		assertThat((List<Post>) model.get("posts"), is(posts));
	}

	@Test
	public void anyListPosts_providesAllCategoriesInModel() {
		controller.listPosts(model, TEST_PAGE);
		assertThat((PostCategory[]) model.get("categories"), is(PostCategory.values()));
	}

	@Test
	public void anyListPosts_providesPaginationInfoInModel(){
		PaginationInfo paginationInfo = new PaginationInfo(new PageRequest(TEST_PAGE,10), 20);
		when(blogService.paginationInfo(new BlogPostsPageRequest(TEST_PAGE-1))).thenReturn(paginationInfo);
		controller.listPosts(model, TEST_PAGE);
		assertThat((PaginationInfo) model.get("paginationInfo"), is(paginationInfo));
	}

	@Test(expected = BlogPostsNotFound.class)
	public void throwsExceptionWhenPostsNotFound() {
		ArrayList<Post> noPosts = new ArrayList<Post>();
		when(blogService.getPublishedPosts(any(Pageable.class))).thenReturn(noPosts);
		controller.listPosts(model, TEST_PAGE);
	}

	@Test
	public void viewNameForAllPosts() {
		assertThat(controller.listPosts(model, TEST_PAGE), is("blog/index"));
	}

	@Test
	public void viewNameForBroadcastPosts() throws Exception {
		assertThat(controller.listBroadcasts(model, TEST_PAGE), is("blog/index"));
	}

	@Test
	public void viewNameForSinglePost() {
		assertThat(controller.showPost(1L, "not important", model), is("blog/show"));
	}

	@Test
	public void viewNameForCategoryPosts() {
		when(blogService.getPublishedPosts(eq(ENGINEERING), any(Pageable.class))).thenReturn(posts);
		String view = controller.listPostsForCategory(ENGINEERING, model, TEST_PAGE);
		assertThat(view, is("blog/index"));
	}

	@Test
	public void singlePostInModelForOnePost() {
		Post post = PostBuilder.post().build();
		when(blogService.getPublishedPost(post.getId())).thenReturn(post);
		controller.showPost(post.getId(), "1-post-title", model);
		assertThat((Post) model.get("post"), is(post));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void postsInModelForAllPosts(){
		controller.listPosts(model, TEST_PAGE);
		assertThatPostsAreInModel();
	}

	@Test
	public void postsInModelForBroadcastPosts() throws Exception {
		controller.listBroadcasts(model, TEST_PAGE);
		assertThatPostsAreInModel();
	}

	@Test
	public void postsInModelForCategoryPosts() {
		when(blogService.getPublishedPosts(eq(ENGINEERING), any(Pageable.class))).thenReturn(posts);
		controller.listPostsForCategory(ENGINEERING, model, TEST_PAGE);
		assertThatPostsAreInModel();
	}

	@Test
	public void postsInModelForAllPostsAtomFeed(){
		controller.atomFeed(model);
		assertThatPostsAreInModel();
	}

	@Test
	public void listPosts_offsetsThePageToBeZeroIndexed(){
		controller.listPosts(model, TEST_PAGE);
		int zeroIndexedPage = TEST_PAGE - 1;
		BlogPostsPageRequest pageRequest = new BlogPostsPageRequest(zeroIndexedPage);
		verify(blogService).getPublishedPosts(eq(pageRequest));
	}
}
