package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.blog.PostFormat;
import sagan.support.DateFactory;
import sagan.support.DateTestUtils;
import sagan.team.MemberProfile;
import sagan.team.support.TeamRepository;

import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class PostFormAdapter_CreatePostTests {

    public static final String RENDERED_HTML = "<p>Rendered HTML</p><p>from Markdown</p>";
    public static final String RENDERED_SUMMARY = "<p>Rendered HTML</p>";
    private static final String AUTHOR_USERNAME = "author";
    private static final String AUTHOR_NAME = "mr author";
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

    @Rule
    public ExpectedException expected = ExpectedException.none();
    private PostForm postForm;

    private PostFormAdapter adapter;

    @Before
    public void setup() {
        MemberProfile profile = new MemberProfile();
        profile.setUsername(AUTHOR_USERNAME);
        profile.setName(AUTHOR_NAME);
        given(teamRepository.findByUsername(AUTHOR_USERNAME)).willReturn(profile);

        given(renderer.render(content, PostFormat.MARKDOWN)).willReturn(RENDERED_HTML);
        given(postSummary.forContent(anyString(), anyInt())).willReturn(RENDERED_SUMMARY);
        given(dateFactory.now()).willReturn(now);

        adapter = new PostFormAdapter(renderer, postSummary, dateFactory, teamRepository);

        postForm = new PostForm();
        postForm.setTitle(title);
        postForm.setContent(content);
        postForm.setCategory(category);
        postForm.setBroadcast(broadcast);
        postForm.setPublishAt(publishAt);
        postForm.setFormat(PostFormat.MARKDOWN);

        post = adapter.createPostFromPostForm(postForm, AUTHOR_USERNAME);
    }

    @Test
    public void postHasCorrectUserEnteredValues() {
        assertThat(post.getTitle(), equalTo(title));
        assertThat(post.getRawContent(), equalTo(content));
        assertThat(post.getCategory(), equalTo(category));
        assertThat(post.isBroadcast(), equalTo(broadcast));
        assertThat(post.isDraft(), equalTo(draft));
        assertThat(post.getPublishAt(), equalTo(publishAt));
    }

    @Test
    public void postHasAuthor() {
        assertThat(post.getAuthor().getName(), equalTo(AUTHOR_NAME));
    }

    @Test
    public void postHasRenderedContent() {
        assertThat(post.getRenderedContent(), equalTo(RENDERED_HTML));
    }

    @Test
    public void postHasRenderedSummary() {
        assertThat(post.getRenderedSummary(), equalTo(RENDERED_SUMMARY));
    }

    @Test
    public void postHasPublicSlug() {
        assertThat(post.getPublicSlug(), equalTo("2013/07/01/title"));
    }

    @Test
    public void draftWithNullPublishDate() {
        postForm.setDraft(true);
        postForm.setPublishAt(null);
        post = adapter.createPostFromPostForm(postForm, AUTHOR_USERNAME);
        assertThat(post.getPublishAt(), is(nullValue()));
    }

    @Test
    public void postWithNullPublishDateSetsPublishAtToNow() {
        postForm.setDraft(false);
        postForm.setPublishAt(null);
        post = adapter.createPostFromPostForm(postForm, AUTHOR_USERNAME);
        assertThat(post.getPublishAt(), equalTo(now));
    }

    @Test
    public void postCreatedDateDefaultsToNow() throws Exception {
        assertThat(post.getCreatedAt(), is(now));
    }

    @Test
    public void postCreatedDateCanBeSetFromAPostForm() throws Exception {
        Date createdAt = DateTestUtils.getDate("2013-05-23 22:58");
        postForm.setCreatedAt(createdAt);
        Post post = adapter.createPostFromPostForm(postForm, AUTHOR_USERNAME);
        assertThat(post.getCreatedAt(), is(createdAt));
    }
}
