package io.spring.site.web.blog.feed;

import io.spring.site.domain.blog.BlogService;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostBuilder;
import io.spring.site.domain.blog.PostCategory;
import io.spring.site.web.PageableFactory;
import io.spring.site.web.blog.feed.BlogFeedController;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;

import static io.spring.site.domain.blog.PostCategory.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

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
		this.controller = new BlogFeedController(this.blogService);
		this.posts.add(PostBuilder.post().build());
		this.page = new PageImpl<Post>(this.posts, mock(Pageable.class), 20);
		given(this.blogService.getPublishedPosts(eq(PageableFactory.forFeeds())))
				.willReturn(this.page);
		given(
				this.blogService.getPublishedPosts(eq(TEST_CATEGORY),
						eq(PageableFactory.forFeeds()))).willReturn(this.page);
		given(this.blogService.getPublishedBroadcastPosts(eq(PageableFactory.forFeeds())))
				.willReturn(this.page);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void postsInModelForAllPublishedPosts() {
		this.controller.listPublishedPosts(this.model, this.response);
		assertThat((List<Post>) this.model.get("posts"), is(this.posts));
	}

	@Test
	public void feedMetadataInModelForAllPublishedPosts() {
		this.controller.listPublishedPosts(this.model, this.response);
		assertThat((String) this.model.get("feed-title"), is("Spring"));
		assertThat((String) this.model.get("feed-path"), is("/blog.atom"));
		assertThat((String) this.model.get("blog-path"), is("/blog"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void postsInModelForPublishedCategoryPosts() {
		this.controller.listPublishedPostsForCategory(TEST_CATEGORY, this.model,
				this.response);
		assertThat((List<Post>) this.model.get("posts"), is(this.posts));
	}

	@Test
	public void feedMetadataInModelForCategoryPosts() {
		this.controller.listPublishedPostsForCategory(TEST_CATEGORY, this.model,
				this.response);
		assertThat((String) this.model.get("feed-title"),
				is("Spring " + TEST_CATEGORY.getDisplayName()));
		assertThat((String) this.model.get("feed-path"), is("/blog/category/"
				+ TEST_CATEGORY.getUrlSlug() + ".atom"));
		assertThat((String) this.model.get("blog-path"), is("/blog/category/"
				+ TEST_CATEGORY.getUrlSlug()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void postsInModelForPublishedBroadcastPosts() {
		this.controller.listPublishedBroadcastPosts(this.model, this.response);
		assertThat((List<Post>) this.model.get("posts"), is(this.posts));
	}

	@Test
	public void feedMetadataInModelForBroadcastPosts() {
		this.controller.listPublishedBroadcastPosts(this.model, this.response);
		assertThat((String) this.model.get("feed-title"), is("Spring Broadcasts"));
		assertThat((String) this.model.get("feed-path"), is("/blog/broadcasts.atom"));
		assertThat((String) this.model.get("blog-path"), is("/blog/broadcasts"));
	}

}
