package sagan.site.blog.support;

import java.text.ParseException;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.site.blog.Post;
import sagan.site.blog.PostBuilder;
import sagan.support.DateFactory;
import sagan.support.DateTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PostViewTests {

    @Mock
    private DateFactory dateFactory;

    private Locale defaultLocale;

    private Post post;
    private PostView postView;

    @BeforeEach
    public void setUp() throws Exception {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void formattedPublishDateForUnscheduledDraft() {
        post = PostBuilder.post().draft().unscheduled().build();
        postView = PostView.of(post, dateFactory);

        assertThat(postView.getFormattedPublishDate()).isEqualTo("Unscheduled");
    }

    @Test
    public void formattedPublishDateForPublishedPosts() throws ParseException {
        post = PostBuilder.post().publishAt("2012-07-02 13:42").build();
        postView = PostView.of(post, dateFactory);

        assertThat(postView.getFormattedPublishDate()).isEqualTo("July 02, 2012");
    }

    @Test
    public void draftPath() throws ParseException {
        given(dateFactory.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
        post = PostBuilder.post().id(123L).title("My Post").draft().build();
        postView = PostView.of(post, dateFactory);

        assertThat(postView.getPath()).isEqualTo("/admin/blog/123-my-post");
    }

    @Test
    public void scheduledPost() throws ParseException {
        given(dateFactory.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
        post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-05 13:42").build();
        postView = PostView.of(post, dateFactory);

        assertThat(postView.getPath()).isEqualTo("/admin/blog/123-my-post");
    }

    @Test
    public void publishedPost() throws ParseException {
        given(dateFactory.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
        post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-01 13:42").build();
        postView = PostView.of(post, dateFactory);

        assertThat(postView.getPath()).isEqualTo("/blog/2012/07/01/my-post");
    }

    @Test
    public void knowsWhenSummaryAndContentDiffer() throws Exception {
        Post post = PostBuilder.post().renderedContent("A string")
                .renderedSummary("A different string")
                .build();

        postView = PostView.of(post, dateFactory);

        assertThat(postView.showReadMore()).isTrue();
    }

    @Test
    public void knowsWhenSummaryAndContentAreEqual() throws Exception {
        String content = "Test content";
        Post post = PostBuilder.post().renderedContent(content)
                .renderedSummary(content)
                .build();

        postView = PostView.of(post, dateFactory);

        assertThat(postView.showReadMore()).isFalse();
    }

}
