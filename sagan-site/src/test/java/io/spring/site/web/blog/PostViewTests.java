package io.spring.site.web.blog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sagan.util.DateTestUtils;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import io.spring.site.domain.services.DateService;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class PostViewTests {

    @Mock
    private DateService dateService;

    private Post post;
    private PostView postView;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void formattedPublishDateForUnscheduledDraft() {
        post = PostBuilder.post().draft().unscheduled().build();
        postView = new PostView(post, dateService);

        assertThat(postView.getFormattedPublishDate(), equalTo("Unscheduled"));
    }

    @Test
    public void formattedPublishDateForPublishedPosts() throws ParseException {
        post = PostBuilder.post().publishAt("2012-07-02 13:42").build();
        postView = new PostView(post, dateService);

        assertThat(postView.getFormattedPublishDate(), equalTo("July 02, 2012"));
    }

    @Test
    public void draftPath() throws ParseException {
        given(dateService.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
        post = PostBuilder.post().id(123L).title("My Post").draft().build();
        postView = new PostView(post, dateService);

        assertThat(postView.getPath(), equalTo("/admin/blog/123-my-post"));
    }

    @Test
    public void scheduledPost() throws ParseException {
        given(dateService.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
        post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-05 13:42").build();
        postView = new PostView(post, dateService);

        assertThat(postView.getPath(), equalTo("/admin/blog/123-my-post"));
    }

    @Test
    public void publishedPost() throws ParseException {
        given(dateService.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
        post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-01 13:42").build();
        postView = new PostView(post, dateService);

        assertThat(postView.getPath(), equalTo("/blog/2012/07/01/my-post"));
    }

    @Test
    public void knowsWhenSummaryAndContentDiffer() throws Exception {
        Post post = PostBuilder.post().renderedContent("A string")
                .renderedSummary("A different string")
                .build();

        postView = new PostView(post, dateService);

        assertThat(postView.showReadMore(), is(true));
    }

    @Test
    public void knowsWhenSummaryAndContentAreEqual() throws Exception {
        String content = "Test content";
        Post post = PostBuilder.post().renderedContent(content)
                .renderedSummary(content)
                .build();

        postView = new PostView(post, dateService);

        assertThat(postView.showReadMore(), is(false));
    }

}
