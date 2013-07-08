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
import org.springframework.site.blog.PostCategory;
import org.springframework.site.blog.PostForm;
import org.springframework.site.services.DateService;
import org.springframework.site.web.PageableFactory;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.MapBindingResult;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

		when(blogService.getPublishedPosts(any(PageRequest.class))).thenReturn(published);
		when(blogService.getDraftPosts(any(PageRequest.class))).thenReturn(drafts);
		when(blogService.getScheduledPosts(any(PageRequest.class))).thenReturn(scheduled);

		Page<PostView> draftViews = new PageImpl<PostView>(new ArrayList<PostView>());
		Page<PostView> publishedViews = new PageImpl<PostView>(new ArrayList<PostView>());
		Page<PostView> scheduledViews = new PageImpl<PostView>(new ArrayList<PostView>());

		when(postViewFactory.createPostViewPage(same(drafts))).thenReturn(draftViews);
		when(postViewFactory.createPostViewPage(same(published))).thenReturn(publishedViews);
		when(postViewFactory.createPostViewPage(same(scheduled))).thenReturn(scheduledViews);

		ExtendedModelMap model = new ExtendedModelMap();
		controller.dashboard(model);

		assertThat((Page<PostView>) model.get("drafts"), sameInstance(draftViews));
		assertThat((Page<PostView>)model.get("posts"), sameInstance(publishedViews));
		assertThat((Page<PostView>)model.get("scheduled"), sameInstance(scheduledViews));
	}

	@Test
	public void showPostModel() {
		Post post = PostBuilder.post().build();
		when(blogService.getPost(post.getId())).thenReturn(post);
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

		when(blogService.addPost(eq(postForm), anyString())).thenReturn(TEST_POST);
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
		when(blogService.addPost(postForm, principal.getName())).thenReturn(post);
		String result = controller.createPost(principal, postForm, bindingResult, null);

		assertThat(result, equalTo("redirect:/blog/123-post-title"));
	}


}
