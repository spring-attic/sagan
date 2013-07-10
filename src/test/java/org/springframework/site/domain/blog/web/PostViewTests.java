package org.springframework.site.domain.blog.web;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.test.DateTestUtils;
import org.springframework.site.web.blog.PostView;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

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
		given(dateService.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
		post = PostBuilder.post().id(123L).title("My Post").draft().build();
		postView = new PostView(post, dateService);

		assertThat(postView.getPath(), equalTo("/admin/blog/123-my-post"));
	}

	@Test
	public void scheduledPost() throws ParseException {
		given(dateService.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
		post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-05 13:42").build();
		postView = new PostView(post, dateService);

		assertThat(postView.getPath(), equalTo("/admin/blog/123-my-post"));
	}

	@Test
	public void publishedPost() throws ParseException {
		given(dateService.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
		post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-01 13:42").build();
		postView = new PostView(post, dateService);

		assertThat(postView.getPath(), equalTo("/blog/123-my-post"));
	}

}
