package org.springframework.site.web.blog.feed;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.web.PageableFactory;
import org.springframework.ui.ExtendedModelMap;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.site.domain.blog.PostCategory.ENGINEERING;

public class BlogFeedControllerTests {

	public static final PostCategory TEST_CATEGORY = ENGINEERING;

	@Mock
	private BlogService blogService;

	private BlogFeedController controller;
	private ExtendedModelMap model = new ExtendedModelMap();
	private Page<Post> page;
	private List<Post> posts = new ArrayList<Post>();
	private HttpServletResponse response = new MockHttpServletResponse();


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new BlogFeedController(blogService);
		posts.add(PostBuilder.post().build());
		page = new PageImpl<Post>(posts, mock(Pageable.class), 20);
		when(blogService.getPublishedPosts(eq(PageableFactory.forFeeds()))).thenReturn(page);
		when(blogService.getPublishedPosts(eq(TEST_CATEGORY), eq(PageableFactory.forFeeds()))).thenReturn(page);
		when(blogService.getPublishedBroadcastPosts(eq(PageableFactory.forFeeds()))).thenReturn(page);
	}

	@Test
	public void postsInModelForAllPublishedPosts(){
		controller.listPublishedPosts(model, response);
		assertThat((List<Post>) model.get("posts"), is(posts));
	}

	@Test
	public void feedMetadataInModelForAllPublishedPosts(){
		controller.listPublishedPosts(model, response);
		assertThat((String) model.get("feed-title"), is("Spring"));
		assertThat((String) model.get("feed-path"), is("/blog.atom"));
		assertThat((String) model.get("blog-path"), is("/blog"));
	}

	@Test
	public void postsInModelForPublishedCategoryPosts(){
		controller.listPublishedPostsForCategory(TEST_CATEGORY, model, response);
		assertThat((List<Post>) model.get("posts"), is(posts));
	}

	@Test
	public void feedMetadataInModelForCategoryPosts(){
		controller.listPublishedPostsForCategory(TEST_CATEGORY, model, response);
		assertThat((String) model.get("feed-title"), is("Spring " + TEST_CATEGORY.getDisplayName()));
		assertThat((String) model.get("feed-path"), is("/blog/category/" + TEST_CATEGORY.getUrlSlug() + ".atom"));
		assertThat((String) model.get("blog-path"), is("/blog/category/" + TEST_CATEGORY.getUrlSlug()));
	}

	@Test
	public void postsInModelForPublishedBroadcastPosts(){
		controller.listPublishedBroadcastPosts(model, response);
		assertThat((List<Post>) model.get("posts"), is(posts));
	}

	@Test
	public void feedMetadataInModelForBroadcastPosts(){
		controller.listPublishedBroadcastPosts(model, response);
		assertThat((String) model.get("feed-title"), is("Spring Broadcasts"));
		assertThat((String) model.get("feed-path"), is("/blog/broadcasts.atom"));
		assertThat((String) model.get("blog-path"), is("/blog/broadcasts"));
	}

}
