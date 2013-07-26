package org.springframework.site.domain.blog.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.web.blog.BlogController;
import org.springframework.site.web.blog.PostView;
import org.springframework.site.web.blog.PostViewFactory;
import org.springframework.ui.ExtendedModelMap;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

public class BlogController_ShowTests {

	@Mock
	private BlogService blogService;

	@Mock
	private HttpServletRequest request;

	private PostViewFactory postViewFactory;

	private BlogController controller;
	private ExtendedModelMap model = new ExtendedModelMap();
	private String viewName;
	private Post post;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		postViewFactory = new PostViewFactory(new DateService());

		controller = new BlogController(blogService, postViewFactory);

		post = PostBuilder.post().build();
		given(blogService.getPublishedPost(post.getId())).willReturn(post);
		viewName = controller.showPost(post.getId(), "1-post-title", model);
	}

	@Test
	public void providesActiveCategoryInModel() {
		assertThat((String) model.get("activeCategory"), equalTo(post.getCategory().getDisplayName()));
	}

	@Test
	public void providesAllCategoriesInModel() {
		assertThat((PostCategory[]) model.get("categories"), is(PostCategory.values()));
	}

	@Test
	public void viewNameIsShow() {
		assertThat(viewName, is("blog/show"));
	}

	@Test
	public void singlePostInModelForOnePost() {
		assertThat(((PostView) model.get("post")).getId(), is(post.getId()));
	}
}
