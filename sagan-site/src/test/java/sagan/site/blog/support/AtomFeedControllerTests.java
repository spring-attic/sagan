package sagan.site.blog.support;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.site.blog.BlogService;
import sagan.site.blog.Post;
import sagan.site.blog.PostBuilder;
import sagan.site.blog.PostCategory;
import sagan.site.support.DateFactory;
import sagan.site.support.nav.PageableFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static sagan.site.blog.PostCategory.ENGINEERING;

/**
 * Unit tests for {@link AtomFeedController}.
 */
@ExtendWith(MockitoExtension.class)
public class AtomFeedControllerTests {

    public static final PostCategory TEST_CATEGORY = ENGINEERING;

    @Mock
    private BlogService blogService;

    @Mock
    private SiteUrl siteUrl;

    @Mock
    private DateFactory dateFactory;

    private AtomFeedController controller;
    private ExtendedModelMap model = new ExtendedModelMap();
    private Page<Post> page;
    private List<Post> posts = new ArrayList<>();
    private HttpServletResponse response = new MockHttpServletResponse();

    @BeforeEach
    public void setUp() throws Exception {
        controller = new AtomFeedController(blogService, siteUrl, dateFactory);
        posts.add(PostBuilder.post().build());
        page = new PageImpl<>(posts, mock(Pageable.class), 20);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModelForAllPublishedPosts() {
		given(blogService.getPublishedPosts(eq(PageableFactory.forFeeds()))).willReturn(page);
        controller.listPublishedPosts(model, response);
        assertThat((List<Post>) model.get("posts")).isEqualTo(posts);
    }

    @Test
    public void feedMetadataInModelForAllPublishedPosts() {
		given(blogService.getPublishedPosts(eq(PageableFactory.forFeeds()))).willReturn(page);
        controller.listPublishedPosts(model, response);
        assertThat((String) model.get("feed-title")).isEqualTo("Spring");
        assertThat((String) model.get("feed-path")).isEqualTo("/blog.atom");
        assertThat((String) model.get("blog-path")).isEqualTo("/blog");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModelForPublishedCategoryPosts() {
		given(blogService.getPublishedPosts(eq(TEST_CATEGORY), eq(PageableFactory.forFeeds()))).willReturn(page);
        controller.listPublishedPostsForCategory(TEST_CATEGORY, model, response);
        assertThat((List<Post>) model.get("posts")).isEqualTo(posts);
    }

    @Test
    public void feedMetadataInModelForCategoryPosts() {
		given(blogService.getPublishedPosts(eq(TEST_CATEGORY), eq(PageableFactory.forFeeds()))).willReturn(page);
        controller.listPublishedPostsForCategory(TEST_CATEGORY, model, response);
        assertThat((String) model.get("feed-title")).isEqualTo("Spring " + TEST_CATEGORY.getDisplayName());
        assertThat((String) model.get("feed-path")).isEqualTo("/blog/category/" + TEST_CATEGORY.getUrlSlug() + ".atom");
        assertThat((String) model.get("blog-path")).isEqualTo("/blog/category/" + TEST_CATEGORY.getUrlSlug());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModelForPublishedBroadcastPosts() {
		given(blogService.getPublishedBroadcastPosts(eq(PageableFactory.forFeeds()))).willReturn(page);
        controller.listPublishedBroadcastPosts(model, response);
        assertThat((List<Post>) model.get("posts")).isEqualTo(posts);
    }

    @Test
    public void feedMetadataInModelForBroadcastPosts() {
		given(blogService.getPublishedBroadcastPosts(eq(PageableFactory.forFeeds()))).willReturn(page);
        controller.listPublishedBroadcastPosts(model, response);
        assertThat((String) model.get("feed-title")).isEqualTo("Spring Broadcasts");
        assertThat((String) model.get("feed-path")).isEqualTo("/blog/broadcasts.atom");
        assertThat((String) model.get("blog-path")).isEqualTo("/blog/broadcasts");
    }

}
