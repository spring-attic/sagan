package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static sagan.blog.PostCategory.ENGINEERING;

/**
 * Unit tests for {@link AtomFeedController}.
 */
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new AtomFeedController(blogService, siteUrl, dateFactory);
        posts.add(PostBuilder.post().build());
        page = new PageImpl<>(posts, mock(Pageable.class), 20);
        given(blogService.getPublishedPosts(eq(PageableFactory.forFeeds()))).willReturn(page);
        given(blogService.getPublishedPosts(eq(TEST_CATEGORY), eq(PageableFactory.forFeeds()))).willReturn(page);
        given(blogService.getPublishedBroadcastPosts(eq(PageableFactory.forFeeds()))).willReturn(page);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModelForAllPublishedPosts() {
        controller.listPublishedPosts(model, response);
        assertThat((List<Post>) model.get("posts"), is(posts));
    }

    @Test
    public void feedMetadataInModelForAllPublishedPosts() {
        controller.listPublishedPosts(model, response);
        assertThat((String) model.get("feed-title"), is("Spring"));
        assertThat((String) model.get("feed-path"), is("/blog.atom"));
        assertThat((String) model.get("blog-path"), is("/blog"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModelForPublishedCategoryPosts() {
        controller.listPublishedPostsForCategory(TEST_CATEGORY, model, response);
        assertThat((List<Post>) model.get("posts"), is(posts));
    }

    @Test
    public void feedMetadataInModelForCategoryPosts() {
        controller.listPublishedPostsForCategory(TEST_CATEGORY, model, response);
        assertThat((String) model.get("feed-title"), is("Spring " + TEST_CATEGORY.getDisplayName()));
        assertThat((String) model.get("feed-path"), is("/blog/category/" + TEST_CATEGORY.getUrlSlug() + ".atom"));
        assertThat((String) model.get("blog-path"), is("/blog/category/" + TEST_CATEGORY.getUrlSlug()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModelForPublishedBroadcastPosts() {
        controller.listPublishedBroadcastPosts(model, response);
        assertThat((List<Post>) model.get("posts"), is(posts));
    }

    @Test
    public void feedMetadataInModelForBroadcastPosts() {
        controller.listPublishedBroadcastPosts(model, response);
        assertThat((String) model.get("feed-title"), is("Spring Broadcasts"));
        assertThat((String) model.get("feed-path"), is("/blog/broadcasts.atom"));
        assertThat((String) model.get("blog-path"), is("/blog/broadcasts"));
    }

}
