package io.spring.site.domain.blog.web;

import io.spring.site.domain.blog.BlogService;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostBuilder;
import io.spring.site.domain.blog.PostCategory;
import io.spring.site.domain.services.DateService;
import io.spring.site.web.blog.BlogController;
import io.spring.site.web.blog.PostView;
import io.spring.site.web.blog.PostViewFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

		post = PostBuilder.post().publishAt("2012-02-01 11:00").build();
		given(blogService.getPublishedPost("2012/02/01/title")).willReturn(post);
		viewName = controller.showPost(
				"2012", "02", "01",
				"title", model);
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
