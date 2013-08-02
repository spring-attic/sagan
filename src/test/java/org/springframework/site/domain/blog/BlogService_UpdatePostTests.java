package org.springframework.site.domain.blog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.site.test.DateTestUtils;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_UpdatePostTests {

	public static final String RENDERED_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p><p>from Markdown</p>";
	public static final String RENDERED_SUMMARY_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p>";
	private BlogService service;
	private Post post;
	private String title = "Title";
	private String content = "Rendered HTML\n\nfrom Markdown";
	private String summary = "Rendered HTML";
	private PostCategory category = PostCategory.ENGINEERING;
	private boolean broadcast = true;
	private boolean draft = false;
	private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");

	private Date now = DateTestUtils.getDate("2013-07-01 13:00");

	@Mock
	private PostRepository postRepository;

	@Mock
	private DateService dateService;

	@Mock
	private TeamRepository teamRepository;

	@Mock
	private SearchService searchService;

	@Mock
	private BlogPostContentRenderer postContentRenderer = mock(BlogPostContentRenderer.class);

	@Mock
	private SummaryExtractor summaryExtractor;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	private PostForm postForm;
	private String ORIGINAL_AUTHOR = "original author";


	@Before
	public void setup() {
		given(this.dateService.now()).willReturn(this.now);

		given(summaryExtractor.extract(anyString(), anyInt())).willReturn(RENDERED_SUMMARY_HTML_FROM_MARKDOWN);

		this.service = new BlogService(this.postRepository, postContentRenderer,
				this.dateService, this.teamRepository, this.searchService, summaryExtractor);
		given(postContentRenderer.render(this.content)).willReturn(
				RENDERED_HTML_FROM_MARKDOWN);
		given(postContentRenderer.render(this.summary)).willReturn(
				RENDERED_SUMMARY_HTML_FROM_MARKDOWN);

		this.post = PostBuilder.post().id(123L).author("author_id", this.ORIGINAL_AUTHOR)
				.build();
		this.postForm = new PostForm(this.post);
		this.postForm.setTitle(this.title);
		this.postForm.setContent(this.content);
		this.postForm.setCategory(this.category);
		this.postForm.setBroadcast(this.broadcast);
		this.postForm.setPublishAt(this.publishAt);

		this.service.updatePost(this.post, this.postForm);
	}

	public void postHasCorrectUserEnteredValues() {
		this.service.updatePost(this.post, this.postForm);

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
		assertThat(this.post.getRenderedContent(), equalTo(RENDERED_HTML_FROM_MARKDOWN));
	}

	@Test
	public void postHasRenderedSummary() {
		assertThat(this.post.getRenderedSummary(),
				equalTo(RENDERED_SUMMARY_HTML_FROM_MARKDOWN));
	}

	@Test
	public void draftWithNullPublishDate() {
		this.postForm.setDraft(true);
		this.postForm.setPublishAt(null);
		this.service.updatePost(this.post, this.postForm);
		assertThat(this.post.getPublishAt(), is(nullValue()));
	}

	@Test
	public void postWithNullPublishDateSetsPublishAtToNow() {
		this.postForm.setDraft(false);
		this.postForm.setPublishAt(null);
		this.service.updatePost(this.post, this.postForm);
		assertThat(this.post.getPublishAt(), equalTo(this.now));
	}

	@Test
	public void postIsPersisted() {
		verify(this.postRepository).save(this.post);
	}

	@Test
	public void updatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
		verify(this.searchService).saveToIndex((SearchEntry) anyObject());
	}

	@Test
	public void updatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
		reset(this.searchService);
		long postId = 123L;
		Post post = PostBuilder.post().id(postId).draft().build();
		this.service.updatePost(post, new PostForm(post));
		verifyZeroInteractions(this.searchService);
	}

	@Test
	public void updatingABlogPost_doesNotChangeItsCreatedDateByDefault() throws Exception {
		Date originalDate = DateTestUtils.getDate("2009-11-20 07:00");
		Post post = PostBuilder.post().createdAt(originalDate).build();
		this.service.updatePost(post, this.postForm);
		assertThat(post.getCreatedAt(), is(originalDate));
	}

	@Test
	public void updatingABlogPost_usesTheCreatedDateFromThePostFormIfPresent() throws Exception {
		Date originalDate = DateTestUtils.getDate("2009-11-20 07:00");
		Post post = PostBuilder.post().createdAt(originalDate).build();

		Date newDate = DateTestUtils.getDate("2010-01-11 03:00");
		postForm.setCreatedAt(newDate);

		this.service.updatePost(post, this.postForm);
		assertThat(post.getCreatedAt(), is(newDate));
	}
}
