package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.support.ViewHelper;
import sagan.support.nav.PageableFactory;
import sagan.support.time.DateTimeFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;

public class BlogController_BroadcastPostsTests {

    private static final int TEST_PAGE = 1;

    @Mock
    private BlogService blogService;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private BlogController controller;
    private DateTimeFactory dateTimeFactory = DateTimeFactory.withDefaultTimeZone();
    private ViewHelper viewHelper = new ViewHelper(Locale.US);
    private ExtendedModelMap model = new ExtendedModelMap();
    private List<PostView> posts = new ArrayList<>();
    private Page<PostView> page;
    private Post post;
    private String viewName;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new BlogController(blogService, dateTimeFactory, viewHelper);

        List<Post> posts = new ArrayList<>();
        post = PostBuilder.post().title("post title").build();
        posts.add(post);
        Page<Post> postsPage = new PageImpl<>(posts, new PageRequest(TEST_PAGE, 10), 20);
        Pageable testPageable = PageableFactory.forLists(TEST_PAGE);

        page = new PageImpl<>(new ArrayList<>(), testPageable, 1);

        given(blogService.getPublishedBroadcastPosts(eq(testPageable))).willReturn(postsPage);
        request.setServletPath("/blog");

        viewName = controller.listPublishedBroadcasts(model, TEST_PAGE);
    }

    @Test
    public void providesAllCategoriesInModel() {
        assertThat(model.get("categories"), is(PostCategory.values()));
    }

    @Test
    public void providesPaginationInfoInModel() {
        assertThat(model.get("paginationInfo"), notNullValue());
    }

    @Test
    public void viewNameIsIndex() throws Exception {
        assertThat(viewName, is("blog/index"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModel() throws Exception {
        controller.listPublishedBroadcasts(model, TEST_PAGE);
        assertThat(((List<PostView>) model.get("posts")).get(0).getTitle(), is("post title"));
    }

}
