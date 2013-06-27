package org.springframework.site.blog.feed;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.site.blog.*;
import org.springframework.site.blog.web.BlogPostsPageRequest;
import org.springframework.site.blog.web.ResultList;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.site.blog.PostCategory.ENGINEERING;

public class BlogFeedControllerTests {

	public static final PostCategory TEST_CATEGORY = ENGINEERING;

	@Mock
	BlogService blogService;
	private BlogFeedController controller;
	private ExtendedModelMap model = new ExtendedModelMap();
	private final List<Post> posts = new ArrayList<Post>();
	private final ResultList<Post> results = new ResultList<Post>(posts, mock(PaginationInfo.class));

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new BlogFeedController(blogService);
		posts.add(PostBuilder.post().build());
	}

	@Test
	public void postsInModelForAllPublishedPosts(){
		when(blogService.getPublishedPosts(eq(BlogPostsPageRequest.forFeeds()))).thenReturn(results);
		controller.listPublishedPosts(model);
		assertThat((List<Post>) model.get("posts"), is(posts));
	}

	@Test
	public void feedMetadataInModelForAllPublishedPosts(){
		when(blogService.getPublishedPosts(eq(BlogPostsPageRequest.forFeeds()))).thenReturn(results);
		controller.listPublishedPosts(model);
		assertThat((String) model.get("feed-title"), is("Spring"));
		assertThat((String) model.get("feed-path"), is("/blog"));
	}

	@Test
	public void postsInModelForPublishedCategoryPosts(){
		when(blogService.getPublishedPosts(eq(TEST_CATEGORY), eq(BlogPostsPageRequest.forFeeds()))).thenReturn(results);
		controller.listPublishedPostsForCategory(TEST_CATEGORY, model);
		assertThat((List<Post>) model.get("posts"), is(posts));
	}

	@Test
	public void feedMetadataInModelForCategoryPosts(){
		when(blogService.getPublishedPosts(eq(TEST_CATEGORY),eq(BlogPostsPageRequest.forFeeds()))).thenReturn(results);
		controller.listPublishedPostsForCategory(TEST_CATEGORY, model);
		assertThat((String) model.get("feed-title"), is("Spring " + TEST_CATEGORY.getDisplayName()));
		assertThat((String) model.get("feed-path"), is("/blog/category/" + TEST_CATEGORY.getUrlSlug()));
	}

	@Test
	public void postsInModelForPublishedBroadcastPosts(){
		when(blogService.getPublishedBroadcastPosts(eq(BlogPostsPageRequest.forFeeds()))).thenReturn(results);
		controller.listPublishedBroadcastPosts(model);
		assertThat((List<Post>) model.get("posts"), is(posts));
	}

	@Test
	public void feedMetadataInModelForBroadcastPosts(){
		when(blogService.getPublishedBroadcastPosts(eq(BlogPostsPageRequest.forFeeds()))).thenReturn(results);
		controller.listPublishedBroadcastPosts(model);
		assertThat((String) model.get("feed-title"), is("Spring Broadcasts"));
		assertThat((String) model.get("feed-path"), is("/blog/broadcasts"));
	}

}
