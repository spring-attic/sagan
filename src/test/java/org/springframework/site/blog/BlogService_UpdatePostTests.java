package org.springframework.site.blog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.search.SearchEntry;
import org.springframework.search.SearchService;
import org.springframework.site.services.DateService;
import org.springframework.site.services.MarkdownService;
import org.springframework.site.team.TeamRepository;
import org.springframework.site.test.DateTestUtils;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

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
		when(dateService.now()).thenReturn(now);

		service = new BlogService(postRepository, markdownService, dateService, teamRepository, searchService);
		when(markdownService.renderToHtml(content)).thenReturn(RENDERED_HTML_FROM_MARKDOWN);
		when(markdownService.renderToHtml(firstParagraph)).thenReturn(RENDERED_SUMMARY_HTML_FROM_MARKDOWN);

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
		verify(searchService).saveToIndex(any(SearchEntry.class));
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
