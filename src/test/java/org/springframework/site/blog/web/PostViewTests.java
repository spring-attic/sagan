package org.springframework.site.blog.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.services.DateService;
import org.springframework.site.test.DateTestUtils;

import java.text.ParseException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class PostViewTests {

	@Mock
	private DateService dateService;

	private Post post;
	private PostView postView;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void formattedPublishDateForUnscheduledDraft() {
		post = PostBuilder.post().draft().unscheduled().build();
		postView = new PostView(post, dateService);

		assertThat(postView.getFormattedPublishDate(), equalTo("Unscheduled"));
	}

	@Test
	public void formattedPublishDateForPublishedPosts() throws ParseException {
		post = PostBuilder.post().publishAt("2012-07-02 13:42").build();
		postView = new PostView(post, dateService);

		assertThat(postView.getFormattedPublishDate(), equalTo("July 02, 2012"));
	}

	@Test
	public void draftPath() throws ParseException {
		when(dateService.now()).thenReturn(DateTestUtils.getDate("2012-07-02 13:42"));
		post = PostBuilder.post().id(123L).title("My Post").draft().build();
		postView = new PostView(post, dateService);

		assertThat(postView.getPath(), equalTo("/admin/blog/123-my-post"));
	}

	@Test
	public void scheduledPost() throws ParseException {
		when(dateService.now()).thenReturn(DateTestUtils.getDate("2012-07-02 13:42"));
		post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-05 13:42").build();
		postView = new PostView(post, dateService);

		assertThat(postView.getPath(), equalTo("/admin/blog/123-my-post"));
	}

	@Test
	public void publishedPost() throws ParseException {
		when(dateService.now()).thenReturn(DateTestUtils.getDate("2012-07-02 13:42"));
		post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-01 13:42").build();
		postView = new PostView(post, dateService);

		assertThat(postView.getPath(), equalTo("/blog/123-my-post"));
	}

}
