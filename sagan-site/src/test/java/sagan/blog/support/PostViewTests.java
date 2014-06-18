package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.support.ViewHelper;
import sagan.support.time.DateTimeFactory;
import sagan.support.DateTimeTestUtils;

import java.text.ParseException;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sagan.support.time.DateTimeUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class PostViewTests {

    private DateTimeFactory dateTimeFactory = DateTimeTestUtils.createFixedTimeFactory("2012-07-02 13:42");

    private ViewHelper viewHelper = new ViewHelper(Locale.US);

    private Post post;
    private PostView postView;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void formattedPublishDateForUnscheduledDraft() {
        post = PostBuilder.post().draft().unscheduled().build();
        postView = PostView.of(post, dateTimeFactory, viewHelper);

        assertThat(postView.getFormattedPublishDate(), equalTo("Unscheduled"));
    }

    @Test
    public void formattedPublishDateForPublishedPosts() throws ParseException {
        post = PostBuilder.post().publishAt("2012-07-02 13:42").build();
        postView = PostView.of(post, dateTimeFactory, viewHelper);

        assertThat(postView.getFormattedPublishDate(), equalTo("July 2, 2012"));
    }

    @Test
    public void draftPath() throws ParseException {
        post = PostBuilder.post().id(123L).title("My Post").draft().build();
        postView = PostView.of(post, dateTimeFactory, viewHelper);

        assertThat(postView.getPath(), equalTo("/admin/blog/123-my-post"));
    }

    @Test
    public void scheduledPost() throws ParseException {
        post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-05 13:42").build();
        postView = PostView.of(post, dateTimeFactory, viewHelper);

        assertThat(postView.getPath(), equalTo("/admin/blog/123-my-post"));
    }

    @Test
    public void publishedPost() throws ParseException {
        post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-01 13:42").build();
        postView = PostView.of(post, dateTimeFactory, viewHelper);

        assertThat(postView.getPath(), equalTo("/blog/2012/07/01/my-post"));
    }

    @Test
    public void knowsWhenSummaryAndContentDiffer() throws Exception {
        Post post = PostBuilder.post().renderedContent("A string")
                .renderedSummary("A different string")
                .build();

        postView = PostView.of(post, dateTimeFactory, viewHelper);

        assertThat(postView.showReadMore(), is(true));
    }

    @Test
    public void knowsWhenSummaryAndContentAreEqual() throws Exception {
        String content = "Test content";
        Post post = PostBuilder.post().renderedContent(content)
                .renderedSummary(content)
                .build();

        postView = PostView.of(post, dateTimeFactory, viewHelper);

        assertThat(postView.showReadMore(), is(false));
    }

}
