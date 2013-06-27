package org.springframework.site.blog.web;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.site.blog.*;
import org.springframework.ui.ExtendedModelMap;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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

	@Test
	public void creatingABlogPostRecordsTheUser() {
		PostForm postForm = new PostForm();
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return "testUser";
			}
		};

		when(blogService.addPost(postForm, "testUser")).thenReturn(PostBuilder.post().build());
		controller.createPost(postForm, principal);
		verify(blogService).addPost(postForm, "testUser");
	}
}
