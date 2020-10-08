package sagan.site.blog;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.site.support.DateFactory;
import sagan.site.support.DateTestUtils;
import sagan.site.team.MemberProfile;
import sagan.site.team.support.TeamRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PostFormAdapter_CreatePostTests {

    public static final String RENDERED_HTML = "<p>Rendered HTML</p><p>from Markdown</p>";
    public static final String RENDERED_SUMMARY = "<p>Rendered HTML</p>";
    private static final String AUTHOR_USERNAME = "author";
    private static final String AUTHOR_NAME = "mr author";
    private MemberProfile profile;
    private Post post;
    private String title = "Title";
    private String content = "Rendered HTML\n\nfrom Markdown";
    private PostCategory category = PostCategory.ENGINEERING;
    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");
    private boolean broadcast = true;
    private boolean draft = false;
    private Date now = DateTestUtils.getDate("2013-07-01 13:00");

    @Mock
    private DateFactory dateFactory;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PostSummary postSummary;

    @Mock
    private PostContentRenderer renderer;

    private PostForm postForm;

    private PostFormAdapter adapter;

    @BeforeEach
    public void setup() {
        this.profile = new MemberProfile();
        profile.setUsername(AUTHOR_USERNAME);
        profile.setName(AUTHOR_NAME);
        given(renderer.render(content, PostFormat.MARKDOWN)).willReturn(RENDERED_HTML);
        given(postSummary.forContent(anyString(), anyInt())).willReturn(RENDERED_SUMMARY);
        given(dateFactory.now()).willReturn(now);

        adapter = new PostFormAdapter(renderer, postSummary, dateFactory);

        postForm = new PostForm();
        postForm.setTitle(title);
        postForm.setContent(content);
        postForm.setCategory(category);
        postForm.setBroadcast(broadcast);
        postForm.setPublishAt(publishAt);
        postForm.setFormat(PostFormat.MARKDOWN);

        post = adapter.createPostFromPostForm(postForm, profile);
    }

    @Test
    public void postHasCorrectUserEnteredValues() {
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getRawContent()).isEqualTo(content);
        assertThat(post.getCategory()).isEqualTo(category);
        assertThat(post.isBroadcast()).isEqualTo(broadcast);
        assertThat(post.isDraft()).isEqualTo(draft);
        assertThat(post.getPublishAt()).isEqualTo(publishAt);
    }

    @Test
    public void postHasAuthor() {
        assertThat(post.getAuthor().getName()).isEqualTo(AUTHOR_NAME);
    }

    @Test
    public void postHasRenderedContent() {
        assertThat(post.getRenderedContent()).isEqualTo(RENDERED_HTML);
    }

    @Test
    public void postHasRenderedSummary() {
        assertThat(post.getRenderedSummary()).isEqualTo(RENDERED_SUMMARY);
    }

    @Test
    public void postHasPublicSlug() {
        assertThat(post.getPublicSlug()).isEqualTo("2013/07/01/title");
    }

    @Test
    public void draftWithNullPublishDate() {
        postForm.setDraft(true);
        postForm.setPublishAt(null);
        post = adapter.createPostFromPostForm(postForm, profile);
        assertThat(post.getPublishAt()).isNull();
    }

    @Test
    public void postWithNullPublishDateSetsPublishAtToNow() {
        postForm.setDraft(false);
        postForm.setPublishAt(null);
        post = adapter.createPostFromPostForm(postForm, profile);
        assertThat(post.getPublishAt()).isEqualTo(now);
    }

    @Test
    public void postCreatedDateDefaultsToNow() throws Exception {
        assertThat(post.getCreatedAt()).isEqualTo(now);
    }

    @Test
    public void postCreatedDateCanBeSetFromAPostForm() throws Exception {
        Date createdAt = DateTestUtils.getDate("2013-05-23 22:58");
        postForm.setCreatedAt(createdAt);
        Post post = adapter.createPostFromPostForm(postForm, profile);
        assertThat(post.getCreatedAt()).isEqualTo(createdAt);
    }
}
