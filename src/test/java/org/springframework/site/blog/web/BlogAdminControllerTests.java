package org.springframework.site.blog.web;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.blog.PostForm;
import org.springframework.ui.ExtendedModelMap;

import java.security.Principal;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
		Page<Post> drafts = new PageImpl<Post>(new ArrayList<Post>());
		Page<Post> posts = new PageImpl<Post>(new ArrayList<Post>());

		when(blogService.getPagedPublishedPosts(any(PageRequest.class))).thenReturn(posts);
		when(blogService.getDraftPosts(any(PageRequest.class))).thenReturn(drafts);

		ExtendedModelMap model = new ExtendedModelMap();
		controller.dashboard(model, 1);

		assertThat((Page<Post>)model.get("drafts"), sameInstance(drafts));
		assertThat((Page<Post>)model.get("posts"), sameInstance(posts));
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

		when(blogService.addPost(eq(postForm), anyString())).thenReturn(PostBuilder.post().build());
		controller.createPost(postForm, principal);
		verify(blogService).addPost(postForm, "testUser");
	}
}
