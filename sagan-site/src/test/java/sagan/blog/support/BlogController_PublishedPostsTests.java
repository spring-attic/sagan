package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;

import java.util.ArrayList;
import java.util.List;

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

public class BlogController_PublishedPostsTests {

    private static final int TEST_PAGE = 1;

    @Mock
    private BlogService blogService;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private BlogController controller;
    private DateFactory dateFactory = new DateFactory();
    private ExtendedModelMap model = new ExtendedModelMap();
    private List<PostView> posts = new ArrayList<>();
    private Page<PostView> page;
    private String viewName;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new BlogController(blogService, dateFactory);

        List<Post> posts = new ArrayList<>();
        posts.add(PostBuilder.post().title("post title").build());
        Page<Post> postsPage = new PageImpl<>(posts, new PageRequest(TEST_PAGE, 10), 20);
        Pageable testPageable = PageableFactory.forLists(TEST_PAGE);

        page = new PageImpl<>(new ArrayList<>(), testPageable, 1);

        given(blogService.getPublishedPosts(eq(testPageable))).willReturn(postsPage);
        request.setServletPath("/blog");

        viewName = controller.listPublishedPosts(model, TEST_PAGE);
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
    public void viewNameIsIndex() {
        assertThat(viewName, is("blog/index"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModel() {
        assertThat(((List<PostView>) model.get("posts")).get(0).getTitle(), is("post title"));
    }
}
