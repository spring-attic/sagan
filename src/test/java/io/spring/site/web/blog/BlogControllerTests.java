package io.spring.site.web.blog;

import io.spring.site.domain.blog.Post;
import io.spring.site.domain.services.DateService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ExtendedModelMap;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

public class BlogControllerTests {

    private PostViewFactory postViewFactory;

    @Mock
    private CachedBlogService blogService;
    private BlogController blogController;

    private ExtendedModelMap model = new ExtendedModelMap();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.postViewFactory = new PostViewFactory(new DateService());
        PageImpl<Post> page = new PageImpl<>(Collections.<Post> emptyList(),
                new PageRequest(1, 1), 1);
        given(this.blogService.getPublishedPostsByDate(anyInt(), any(Pageable.class)))
                .willReturn(page);
        given(
                this.blogService.getPublishedPostsByDate(anyInt(), anyInt(),
                        any(Pageable.class))).willReturn(page);
        given(
                this.blogService.getPublishedPostsByDate(anyInt(), anyInt(), anyInt(),
                        any(Pageable.class))).willReturn(page);

        this.blogController = new BlogController(this.blogService, this.postViewFactory);
    }

    @Test
    public void titleForBlogYearPage() throws Exception {
        this.blogController.listPublishedPostsForYear(2013, 1, this.model);
        String title = (String) this.model.get("title");
        assertThat(title, equalTo("Archive for 2013"));
    }

    @Test
    public void titleForBlogYearMonthPage() throws Exception {
        this.blogController.listPublishedPostsForYearAndMonth(2013, 1, 1, this.model);
        String title = (String) this.model.get("title");
        assertThat(title, equalTo("Archive for January 2013"));
    }

    @Test
    public void titleForBlogYearMonthDayPage() throws Exception {
        this.blogController.listPublishedPostsForDate(2011, 3, 23, 1, this.model);
        String title = (String) this.model.get("title");
        assertThat(title, equalTo("Archive for March 23, 2011"));
    }
}
