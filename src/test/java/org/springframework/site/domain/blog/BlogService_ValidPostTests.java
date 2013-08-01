package org.springframework.site.domain.blog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.domain.services.MarkdownService;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchException;
import org.springframework.site.search.SearchService;
import org.springframework.site.test.DateTestUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.anyObject;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.reset;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.verifyZeroInteractions;
import static org.mockito.BDDMockito.willThrow;

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
		given(dateService.now()).willReturn(now);

		MemberProfile profile = new MemberProfile();
		profile.setMemberId(AUTHOR_ID);
		profile.setName(AUTHOR_NAME);

		given(teamRepository.findByMemberId(AUTHOR_ID)).willReturn(profile);
		given(postRepository.save((Post) anyObject())).will(new Answer<Post>() {
			@Override
			public Post answer(InvocationOnMock invocation) throws Throwable {
				Post post = (Post)invocation.getArguments()[0];
				ReflectionTestUtils.setField(post, "id", 123L);
				return post;
			}
		});

		service = new BlogService(postRepository, new BlogPostContentRenderer(markdownService), dateService, teamRepository, searchService);
		given(markdownService.renderToHtml(content)).willReturn(RENDERED_HTML_FROM_MARKDOWN);
		given(markdownService.renderToHtml(firstParagraph)).willReturn(RENDERED_SUMMARY_HTML_FROM_MARKDOWN);
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
	public void postHasPublicSlug() {
		assertThat(post.getPublicSlug(), equalTo("2013/07/01/title"));
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
		verify(postRepository).save((Post) anyObject());
	}

	@Test
	public void creatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
		verify(searchService).saveToIndex((SearchEntry) anyObject());
	}

	@Test
	public void blogIsSavedWhenSearchServiceIsDown() {
		reset(searchService);
		willThrow(SearchException.class).given(searchService).saveToIndex((SearchEntry) anyObject());
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

	@Test
	public void postCreatedDateDefaultsToNow() throws Exception {
		assertThat(post.getCreatedAt(), is(now));
	}

	@Test
	public void postCreatedDateCanBeSetFromAPostForm() throws Exception {
		Date createdAt = DateTestUtils.getDate("2013-05-23 22:58");
		postForm.setCreatedAt(createdAt);
		Post post = service.addPost(postForm, AUTHOR_ID);
		assertThat(post.getCreatedAt(), is(createdAt));
	}
}
