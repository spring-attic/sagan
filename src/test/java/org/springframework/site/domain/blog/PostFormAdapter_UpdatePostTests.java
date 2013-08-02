package org.springframework.site.domain.blog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.site.test.DateTestUtils;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
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
	private DateService dateService;

	@Mock
	private TeamRepository teamRepository;

	@Mock
	private BlogPostContentRenderer renderer;

	@Mock
	private SummaryExtractor summaryExtractor;

	private PostFormAdapter postFormAdapter;

	@Before
	public void setup() {
		given(this.dateService.now()).willReturn(this.now);
		given(summaryExtractor.extract(anyString(), anyInt())).willReturn(SUMMARY);
		given(renderer.render(this.content)).willReturn(RENDERED_HTML);

		this.post = PostBuilder.post()
				.id(123L)
				.author("author_id", this.ORIGINAL_AUTHOR)
				.build();

		this.postForm = new PostForm(this.post);
		this.postForm.setTitle(this.title);
		this.postForm.setContent(this.content);
		this.postForm.setCategory(this.category);
		this.postForm.setBroadcast(this.broadcast);
		this.postForm.setPublishAt(this.publishAt);

		postFormAdapter = new PostFormAdapter(renderer, summaryExtractor, dateService, teamRepository);
		postFormAdapter.updatePostFromPostForm(post, postForm);
	}

	public void postHasCorrectUserEnteredValues() {
		assertThat(this.post.getTitle(), equalTo(this.title));
		assertThat(this.post.getRawContent(), equalTo(this.content));
		assertThat(this.post.getCategory(), equalTo(this.category));
		assertThat(this.post.isBroadcast(), equalTo(this.broadcast));
		assertThat(this.post.isDraft(), equalTo(this.draft));
		assertThat(this.post.getPublishAt(), equalTo(this.publishAt));
	}

	@Test
	public void postRetainsOriginalAuthor() {
		assertThat(this.post.getAuthor().getName(), equalTo(this.ORIGINAL_AUTHOR));
	}

	@Test
	public void postHasRenderedContent() {
		assertThat(this.post.getRenderedContent(), equalTo(RENDERED_HTML));
	}

	@Test
	public void postHasRenderedSummary() {
		assertThat(this.post.getRenderedSummary(),
				equalTo(SUMMARY));
	}

	@Test
	public void draftWithNullPublishDate() {
		this.postForm.setDraft(true);
		this.postForm.setPublishAt(null);
		this.postFormAdapter.updatePostFromPostForm(this.post, this.postForm);
		assertThat(this.post.getPublishAt(), is(nullValue()));
	}

	@Test
	public void postWithNullPublishDateSetsPublishAtToNow() {
		this.postForm.setDraft(false);
		this.postForm.setPublishAt(null);
		this.postFormAdapter.updatePostFromPostForm(this.post, this.postForm);
		assertThat(this.post.getPublishAt(), equalTo(this.now));
	}

	@Test
	public void updatingABlogPost_doesNotChangeItsCreatedDateByDefault() throws Exception {
		Date originalDate = DateTestUtils.getDate("2009-11-20 07:00");
		Post post = PostBuilder.post().createdAt(originalDate).build();
		this.postFormAdapter.updatePostFromPostForm(post, this.postForm);
		assertThat(post.getCreatedAt(), is(originalDate));
	}

	@Test
	public void updatingABlogPost_usesTheCreatedDateFromThePostFormIfPresent() throws Exception {
		Date originalDate = DateTestUtils.getDate("2009-11-20 07:00");
		Post post = PostBuilder.post().createdAt(originalDate).build();

		Date newDate = DateTestUtils.getDate("2010-01-11 03:00");
		postForm.setCreatedAt(newDate);

		this.postFormAdapter.updatePostFromPostForm(post, this.postForm);
		assertThat(post.getCreatedAt(), is(newDate));
	}
}
