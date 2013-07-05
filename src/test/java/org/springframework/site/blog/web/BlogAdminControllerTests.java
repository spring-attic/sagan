package org.springframework.site.blog.web;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.site.blog.*;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.site.services.DateService;
import org.springframework.site.web.PageableFactory;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.MapBindingResult;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlogAdminControllerTests {
	private static final Post TEST_POST = PostBuilder.post()
			.id(123L)
			.publishYesterday()
			.build();

	private BlogAdminController controller;

	@Mock
	private BlogService blogService;

	@Mock
	private SearchService searchService;

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
		controller = new BlogAdminController(blogService, postViewFactory, searchService);
	}

	@Test
	public void dashboardShowsUsersPosts() {
		postViewFactory = mock(PostViewFactory.class);
		controller = new BlogAdminController(blogService, postViewFactory, searchService);

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

	@Test
	public void creatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
		PostForm postForm = new PostForm();
		when(blogService.addPost(eq(postForm), anyString())).thenReturn(TEST_POST);
		controller.createPost(principal, postForm, new BindException(postForm, "postForm"), null);
		verify(searchService).saveToIndex(any(SearchEntry.class));
	}

	@Test
	public void creatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
		PostForm postForm = new PostForm();
		Post post = PostBuilder.post().draft().build();
		when(blogService.addPost(eq(postForm), anyString())).thenReturn(post);
		controller.createPost(principal, postForm, new BindException(postForm, "postForm"), null);
		verifyZeroInteractions(searchService);
	}

	@Test
	public void updatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
		long postId = TEST_POST.getId();
		when(blogService.getPost(eq(postId))).thenReturn(TEST_POST);
		controller.updatePost(postId, new PostForm());
		verify(searchService).saveToIndex(any(SearchEntry.class));
	}

	@Test
	public void updatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
		long postId = 123L;
		Post post = PostBuilder.post().id(postId).draft().build();
		when(blogService.getPost(eq(postId))).thenReturn(post);
		controller.updatePost(postId, new PostForm());
		verifyZeroInteractions(searchService);
	}


}
