package org.springframework.site.blog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.search.SearchEntry;
import org.springframework.search.SearchException;
import org.springframework.search.SearchService;
import org.springframework.site.services.DateService;
import org.springframework.site.services.MarkdownService;
import org.springframework.site.team.MemberProfile;
import org.springframework.site.team.TeamRepository;
import org.springframework.site.test.DateTestUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_ValidPostTests {

	public static final String RENDERED_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p><p>from Markdown</p>";
	public static final String RENDERED_SUMMARY_HTML_FROM_MARKDOWN = "<p>Rendered HTML</p>";
	private static final String AUTHOR_ID = "author";
	private static final String AUTHOR_NAME = "mr author";
	private BlogService service;
	private Post post;
	private String title = "Title";
	private String content = "Rendered HTML\n\nfrom Markdown";
	private String firstParagraph = "Rendered HTML";
	private PostCategory category = PostCategory.ENGINEERING;
	private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");
	private boolean broadcast = true;
	private boolean draft = false;

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

	@Before
	public void setup() {
		when(dateService.now()).thenReturn(now);

		MemberProfile profile = new MemberProfile();
		profile.setMemberId(AUTHOR_ID);
		profile.setName(AUTHOR_NAME);

		when(teamRepository.findByMemberId(AUTHOR_ID)).thenReturn(profile);
		when(postRepository.save(any(Post.class))).then(new Answer<Post>() {
			@Override
			public Post answer(InvocationOnMock invocation) throws Throwable {
				Post post = (Post)invocation.getArguments()[0];
				ReflectionTestUtils.setField(post, "id", 123L);
				return post;
			}
		});

		service = new BlogService(postRepository, markdownService, dateService, teamRepository, searchService);
		when(markdownService.renderToHtml(content)).thenReturn(RENDERED_HTML_FROM_MARKDOWN);
		when(markdownService.renderToHtml(firstParagraph)).thenReturn(RENDERED_SUMMARY_HTML_FROM_MARKDOWN);
		postForm = new PostForm();
		postForm.setTitle(title);
		postForm.setContent(content);
		postForm.setCategory(category);
		postForm.setBroadcast(broadcast);
		postForm.setPublishAt(publishAt);
		post = service.addPost(postForm, AUTHOR_ID);
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
		post = service.addPost(postForm, AUTHOR_ID);
		assertThat(post.getPublishAt(), is(nullValue()));
	}

	@Test
	public void postWithNullPublishDateSetsPublishAtToNow() {
		postForm.setDraft(false);
		postForm.setPublishAt(null);
		post = service.addPost(postForm, AUTHOR_ID);
		assertThat(post.getPublishAt(), equalTo(now));
	}

	@Test
	public void postIsPersisted() {
		verify(postRepository).save(any(Post.class));
	}

	@Test
	public void extractFirstParagraph() {
		assertEquals("xx", service.extractFirstParagraph("xxxxx", 2));
		assertEquals("xx", service.extractFirstParagraph("xx\n\nxxx", 20));
		assertEquals("xx", service.extractFirstParagraph("xx xx\n\nxxx", 4));
	}

	@Test
	public void creatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
		verify(searchService).saveToIndex(any(SearchEntry.class));
	}

	@Test
	public void blogIsSavedWhenSearchServiceIsDown() {
		reset(searchService);
		doThrow(SearchException.class).when(searchService).saveToIndex(any(SearchEntry.class));
		post = service.addPost(postForm, AUTHOR_ID);
		verify(postRepository).save(post);
	}

	@Test
	public void creatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
		reset(searchService);
		postForm.setDraft(true);
		service.addPost(postForm, AUTHOR_ID);
		verifyZeroInteractions(searchService);
	}
}
