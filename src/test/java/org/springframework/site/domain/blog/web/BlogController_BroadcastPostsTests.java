package org.springframework.site.domain.blog.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.PaginationInfo;
import org.springframework.site.web.blog.BlogController;
import org.springframework.site.web.blog.PostView;
import org.springframework.site.web.blog.PostViewFactory;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;

public class BlogController_BroadcastPostsTests {

	private static final int TEST_PAGE = 1;

	@Mock
	private BlogService blogService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private PostViewFactory postViewFactory;

	private BlogController controller;
	private ExtendedModelMap model = new ExtendedModelMap();
	private List<PostView> posts = new ArrayList<PostView>();
	private Page<PostView> page;
	private String viewName;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new BlogController(blogService, postViewFactory);

		List<Post> posts = new ArrayList<Post>();
		posts.add(PostBuilder.post().build());
		Page postsPage = new PageImpl<Post>(posts, new PageRequest(TEST_PAGE, 10), 20);
		Pageable testPageable = PageableFactory.forLists(TEST_PAGE);

		page = new PageImpl<PostView>(new ArrayList<PostView>(), testPageable, 1);

		given(blogService.getPublishedBroadcastPosts(eq(testPageable))).willReturn(postsPage);
		given(postViewFactory.createPostViewPage(postsPage)).willReturn(page);
		given(request.getServletPath()).willReturn("/blog/");

		viewName = controller.listPublishedBroadcasts(model, TEST_PAGE, request);
	}

	@Test
	public void providesAllCategoriesInModel() {
		assertThat((PostCategory[]) model.get("categories"), is(PostCategory.values()));
	}

	@Test
	public void providesPaginationInfoInModel(){
		assertThat((PaginationInfo) model.get("paginationInfo"), is(new PaginationInfo(page)));
	}

	@Test
	public void viewNameIsIndex() throws Exception {
		assertThat(viewName, is("blog/index"));
	}

	@Test
	public void postsInModel() throws Exception {
		controller.listPublishedBroadcasts(model, TEST_PAGE, request);
		assertThat((List<PostView>) model.get("posts"), is(posts));
	}

	@Test
	public void feedUrlInModel(){
		String path = "/blog/broadcasts";
		given(request.getServletPath()).willReturn(path);
		controller.listPublishedBroadcasts(model, TEST_PAGE, request);
		assertThat((String) model.get("feed_path"), is(path + ".atom"));
	}


}
