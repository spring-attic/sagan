package org.springframework.site.blog.web;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.PaginationInfo;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.ui.ExtendedModelMap;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BlogAdminControllerTests {
	private BlogAdminController controller;

	@Mock
	private BlogService blogService;

	private ExtendedModelMap model = new ExtendedModelMap();

	@Before
	public void setup() {
		controller = new BlogAdminController(blogService);
	}

	@Test
	public void dashboardShowsUsersPosts() {
		List<Post> posts = Collections.singletonList(PostBuilder.post().build());
		ResultList<Post> results = new ResultList<Post>(posts, mock(PaginationInfo.class));
		when(blogService.getAllPosts(any(PageRequest.class))).thenReturn(results);
		ExtendedModelMap model = new ExtendedModelMap();
		controller.dashboard(model);
		assertThat((List<Post>)model.get("posts"), equalTo(posts));
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
		assertThat(controller.showPost(1L, "not important", model), is("admin/blog/show"));
	}
}
