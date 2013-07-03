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
import org.springframework.site.services.DateService;
import org.springframework.ui.ExtendedModelMap;

import java.security.Principal;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlogAdminControllerTests {
	private BlogAdminController controller;

	@Mock
	private BlogService blogService;

	private ExtendedModelMap model = new ExtendedModelMap();
	private Principal principal;
	private PostViewFactory postViewFactory;

	@Before
	public void setup() {
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

		Page<Post> drafts = new PageImpl<Post>(new ArrayList<Post>(), BlogPostsPageRequest.forDashboard(), 1);
		Page<Post> published = new PageImpl<Post>(new ArrayList<Post>(), BlogPostsPageRequest.forDashboard(), 1);
		Page<Post> scheduled = new PageImpl<Post>(new ArrayList<Post>(), BlogPostsPageRequest.forDashboard(), 1);

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

		when(blogService.addPost(eq(postForm), anyString())).thenReturn(PostBuilder.post().build());
		controller.createPost(postForm, principal);
		verify(blogService).addPost(postForm, "testUser");
	}


	@Test
	public void redirectToPostAfterCreation() throws Exception {
		PostForm postForm = new PostForm();
		Post post = PostBuilder.post().id(123L).publishAt("2013-05-06 00:00").title("Post Title").build();
		when(blogService.addPost(postForm, principal.getName())).thenReturn(post);
		String result = controller.createPost(postForm, principal);

		assertThat(result, equalTo("redirect:/blog/123-post-title"));
	}
}
