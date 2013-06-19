package org.springframework.site.blog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class BlogControllerTests {

	@Mock
	BlogService blogService;
	private BlogController controller;
	private ExtendedModelMap model = new ExtendedModelMap();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new BlogController(blogService);
	}

	@Test
	public void listPostsModel(){
		List<Post> posts = new ArrayList<Post>();
		when(blogService.mostRecentPosts()).thenReturn(posts);
		controller.listPosts(model);
		assertThat((List<Post>) model.get("posts"), is(posts));
	}

	@Test
	public void listPostsView() {
		assertThat(controller.listPosts(model), is("blog/index"));
	}

	@Test
	public void showPostModel() {
		Post post = new Post();
		when(blogService.getPost(post.getId())).thenReturn(post);
		controller.showPost(post.getId(), "1-post-title", model);
		assertThat((Post) model.get("post"), is(post));
	}

	@Test
	public void showPostView() {
		assertThat(controller.showPost(1L, "not important", model), is("blog/show"));
	}

}
