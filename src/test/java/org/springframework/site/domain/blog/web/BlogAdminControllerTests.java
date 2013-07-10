package org.springframework.site.domain.blog.web;


import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.blog.PostForm;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.blog.BlogAdminController;
import org.springframework.site.web.blog.PostView;
import org.springframework.site.web.blog.PostViewFactory;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.MapBindingResult;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class BlogAdminControllerTests {
	private static final Post TEST_POST = PostBuilder.post()
			.id(123L)
			.publishYesterday()
			.build();

	private BlogAdminController controller;

	@Mock
	private BlogService blogService;

	private ExtendedModelMap model = new ExtendedModelMap();
	private Principal principal;
	private PostViewFactory postViewFactory;
	private MapBindingResult bindingResult;

	@Before
	public void setup() {
		bindingResult = new MapBindingResult(new HashMap<Object, Object>(), "postForm");
		postViewFactory = new PostViewFactory(new DateService());
		principal = new Principal() {
			@Override
			public String getName() {
				return "testUser";
			}
		};
		controller = new BlogAdminController(blogService, postViewFactory);
	}

	@Test
	public void dashboardShowsUsersPosts() {
		postViewFactory = mock(PostViewFactory.class);
		controller = new BlogAdminController(blogService, postViewFactory);

		Page<Post> drafts = new PageImpl<Post>(new ArrayList<Post>(), PageableFactory.forDashboard(), 1);
		Page<Post> published = new PageImpl<Post>(new ArrayList<Post>(), PageableFactory.forDashboard(), 1);
		Page<Post> scheduled = new PageImpl<Post>(new ArrayList<Post>(), PageableFactory.forDashboard(), 1);

		given(blogService.getPublishedPosts((PageRequest) anyObject())).willReturn(published);
		given(blogService.getDraftPosts((PageRequest) anyObject())).willReturn(drafts);
		given(blogService.getScheduledPosts((PageRequest) anyObject())).willReturn(scheduled);

		Page<PostView> draftViews = new PageImpl<PostView>(new ArrayList<PostView>());
		Page<PostView> publishedViews = new PageImpl<PostView>(new ArrayList<PostView>());
		Page<PostView> scheduledViews = new PageImpl<PostView>(new ArrayList<PostView>());

		given(postViewFactory.createPostViewPage(same(drafts))).willReturn(draftViews);
		given(postViewFactory.createPostViewPage(same(published))).willReturn(publishedViews);
		given(postViewFactory.createPostViewPage(same(scheduled))).willReturn(scheduledViews);

		ExtendedModelMap model = new ExtendedModelMap();
		controller.dashboard(model);

		assertThat(model.get("drafts"), sameInstance((Object) draftViews));
		assertThat(model.get("posts"), sameInstance((Object) publishedViews));
		assertThat(model.get("scheduled"), sameInstance((Object) scheduledViews));
	}

	@Test
	public void showPostModel() {
		Post post = PostBuilder.post().build();
		given(blogService.getPost(post.getId())).willReturn(post);
		controller.showPost(post.getId(), "1-post-title", model);
		PostView view = (PostView) model.get("post");
		assertThat(view, is(notNullValue()));
	}

	@Test
	public void showPostView() {
		assertThat(controller.showPost(1L, "not important", model), is("admin/blog/show"));
	}

	@Test
	public void creatingABlogPostRecordsTheUser() {
		PostForm postForm = new PostForm();

		given(blogService.addPost(eq(postForm), anyString())).willReturn(TEST_POST);
		controller.createPost(principal, postForm, new BindException(postForm, "postForm"), null);
		verify(blogService).addPost(postForm, "testUser");
	}

	@Test
	public void redirectToPostAfterCreation() throws Exception {
		PostForm postForm = new PostForm();
		postForm.setTitle("title");
		postForm.setContent("content");
		postForm.setCategory(PostCategory.ENGINEERING);
		Post post = PostBuilder.post().id(123L).publishAt("2013-05-06 00:00").title("Post Title").build();
		given(blogService.addPost(postForm, principal.getName())).willReturn(post);
		String result = controller.createPost(principal, postForm, bindingResult, null);

		assertThat(result, equalTo("redirect:/blog/123-post-title"));
	}


}
