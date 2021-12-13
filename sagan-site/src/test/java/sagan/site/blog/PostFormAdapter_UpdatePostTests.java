package sagan.site.blog;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.site.support.DateFactory;
import sagan.site.support.DateTestUtils;
import sagan.site.team.support.TeamRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PostFormAdapter_UpdatePostTests {

    public static final String RENDERED_HTML = "<p>Rendered HTML</p><p>from Markdown</p>";
    public static final String SUMMARY = "<p>Rendered HTML</p>";
    private Post post;
    private String title = "Title";
    private String content = "Rendered HTML\n\nfrom Markdown";
    private PostCategory category = PostCategory.ENGINEERING;
    private boolean broadcast = true;
    private boolean draft = false;
    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");
    private Date now = DateTestUtils.getDate("2013-07-01 13:00");
    private PostForm postForm;
    private String ORIGINAL_AUTHOR = "original author";

    @Mock
    private DateFactory dateFactory;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PostContentRenderer renderer;

    @Mock
    private PostSummary postSummary;

    private PostFormAdapter postFormAdapter;

    @BeforeEach
    public void setup() {
        given(postSummary.forContent(anyString(), anyInt())).willReturn(SUMMARY);
        given(renderer.render(content, PostFormat.MARKDOWN)).willReturn(RENDERED_HTML);

        post = PostBuilder.post().id(123L).author("author_id", ORIGINAL_AUTHOR).build();

        postForm = new PostForm(post);
        postForm.setTitle(title);
        postForm.setContent(content);
        postForm.setCategory(category);
        postForm.setBroadcast(broadcast);
        postForm.setPublishAt(publishAt);

        postFormAdapter = new PostFormAdapter(renderer, postSummary, dateFactory);
        postFormAdapter.updatePostFromPostForm(post, postForm);
    }

    public void postHasCorrectUserEnteredValues() {
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getRawContent()).isEqualTo(content);
        assertThat(post.getCategory()).isEqualTo(category);
        assertThat(post.isBroadcast()).isEqualTo(broadcast);
        assertThat(post.isDraft()).isEqualTo(draft);
        assertThat(post.getPublishAt()).isEqualTo(publishAt);
    }

    @Test
    public void postRetainsOriginalAuthor() {
        assertThat(post.getAuthor().getName()).isEqualTo(ORIGINAL_AUTHOR);
    }

    @Test
    public void postHasRenderedContent() {
        assertThat(post.getRenderedContent()).isEqualTo(RENDERED_HTML);
    }

    @Test
    public void postHasRenderedSummary() {
        assertThat(post.getRenderedSummary()).isEqualTo(SUMMARY);
    }

    @Test
    public void draftWithNullPublishDate() {
        postForm.setDraft(true);
        postForm.setPublishAt(null);
        postFormAdapter.updatePostFromPostForm(post, postForm);
        assertThat(post.getPublishAt()).isNull();
    }

    @Test
    public void postWithNullPublishDateSetsPublishAtToNow() {
		given(dateFactory.now()).willReturn(now);
        postForm.setDraft(false);
        postForm.setPublishAt(null);
        postFormAdapter.updatePostFromPostForm(post, postForm);
        assertThat(post.getPublishAt()).isEqualTo(now);
    }

    @Test
    public void updatingABlogPost_doesNotChangeItsCreatedDateByDefault() throws Exception {
        Date originalDate = DateTestUtils.getDate("2009-11-20 07:00");
        Post post = PostBuilder.post().createdAt(originalDate).build();
        postFormAdapter.updatePostFromPostForm(post, postForm);
        assertThat(post.getCreatedAt()).isEqualTo(originalDate);
    }

    @Test
    public void updatingABlogPost_usesTheCreatedDateFromThePostFormIfPresent() throws Exception {
        Date originalDate = DateTestUtils.getDate("2009-11-20 07:00");
        Post post = PostBuilder.post().createdAt(originalDate).build();

        Date newDate = DateTestUtils.getDate("2010-01-11 03:00");
        postForm.setCreatedAt(newDate);

        postFormAdapter.updatePostFromPostForm(post, postForm);
        assertThat(post.getCreatedAt()).isEqualTo(newDate);
    }
}
