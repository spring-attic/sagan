package org.springframework.site.domain.blog;

import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.domain.services.MarkdownService;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.site.test.DateTestUtils;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_UpdatePostTests {

	public static final String RENDERED_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p><p>from Markdown</p>";
	public static final String RENDERED_SUMMARY_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p>";
	private static final String AUTHOR = "author";
	private BlogService service;
	private Post post;
	private String title = "Title";
	private String content = "Rendered HTML\n\nfrom Markdown";
	private String firstParagraph = "Rendered HTML";
	private PostCategory category = PostCategory.ENGINEERING;
	private boolean broadcast = true;
	private boolean draft = false;
	private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");

	private Date now = DateTestUtils.getDate("2013-07-01 13:00");

	@Mock
	private PostRepository postRepository;

	@Mock
	private MarkdownService markdownService;

	@Mock
	private DateService dateService;

	@Mock
	private TeamRepository teamRepository;

	@Mock
	private SearchService searchService;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	private PostForm postForm;
	private String ORIGINAL_AUTHOR = "original author";

	@Before
	public void setup() {
		given(dateService.now()).willReturn(now);

		service = new BlogService(postRepository, markdownService, dateService, teamRepository, searchService);
		given(markdownService.renderToHtml(content)).willReturn(RENDERED_HTML_FROM_MARKDOWN);
		given(markdownService.renderToHtml(firstParagraph)).willReturn(RENDERED_SUMMARY_HTML_FROM_MARKDOWN);

		post = PostBuilder.post().id(123L).author("author_id", ORIGINAL_AUTHOR).build();
		postForm = new PostForm(post);
		postForm.setTitle(title);
		postForm.setContent(content);
		postForm.setCategory(category);
		postForm.setBroadcast(broadcast);
		postForm.setPublishAt(publishAt);

		service.updatePost(post, postForm);
	}

	public void postHasCorrectUserEnteredValues() {
		service.updatePost(post, postForm);

		assertThat(post.getTitle(), equalTo(title));
		assertThat(post.getRawContent(), equalTo(content));
		assertThat(post.getCategory(), equalTo(category));
		assertThat(post.isBroadcast(), equalTo(broadcast));
		assertThat(post.isDraft(), equalTo(draft));
		assertThat(post.getPublishAt(), equalTo(publishAt));
	}

	@Test
	public void postRetainsOriginalAuthor() {
		assertThat(post.getAuthor().getName(), equalTo(ORIGINAL_AUTHOR));
	}

	@Test
	public void postHasRenderedContent() {
		assertThat(post.getRenderedContent(), equalTo(RENDERED_HTML_FROM_MARKDOWN));
	}

	@Test
	public void postHasRenderedSummary() {
		assertThat(post.getRenderedSummary(), equalTo(RENDERED_SUMMARY_HTML_FROM_MARKDOWN));
	}

	@Test
	public void draftWithNullPublishDate() {
		postForm.setDraft(true);
		postForm.setPublishAt(null);
		service.updatePost(post, postForm);
		assertThat(post.getPublishAt(), is(nullValue()));
	}

	@Test
	public void postWithNullPublishDateSetsPublishAtToNow() {
		postForm.setDraft(false);
		postForm.setPublishAt(null);
		service.updatePost(post, postForm);
		assertThat(post.getPublishAt(), equalTo(now));
	}

	@Test
	public void postIsPersisted() {
		verify(postRepository).save(post);
	}

	@Test
	public void updatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
		verify(searchService).saveToIndex((SearchEntry) anyObject());
	}

	@Test
	public void updatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
		reset(searchService);
		long postId = 123L;
		Post post = PostBuilder.post().id(postId).draft().build();
		service.updatePost(post, new PostForm(post));
		verifyZeroInteractions(searchService);
	}
}
