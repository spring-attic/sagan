package org.springframework.site.blog;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class PostTests {

	@Test
	public void slugReplacesSpacesWithDashes() {
		assertEquals("this-is-a-title", new Post("This is a title", "", PostCategory.ENGINEERING).getSlug());
	}

	@Test
	public void slugReplacesMultipleSpacesWithASingleDash() {
		assertEquals("this-is-a-title", new Post("This    is a title", "", PostCategory.ENGINEERING).getSlug());
	}

	@Test
	public void slugStripsNonAlphanumericCharacters() {
		assertEquals("title-1-with-characters", new Post("Title 1, with characters';:\\|", "", PostCategory.ENGINEERING).getSlug());
	}

	@Test
	public void slugStripsNonAlphanumericCharactersUsedAsDividersWithSpaces() {
		assertEquals("title-1-something", new Post("Title__--1/@something", "", PostCategory.ENGINEERING).getSlug());
	}

	@Test
	public void slugStripsNewLineCharacters() {
		assertEquals("title-1-on-multiple-lines", new Post("Title 1\n on multiple\nlines", "", PostCategory.ENGINEERING).getSlug());
	}

	@Test
	public void pathToFullContent(){
		Post post = spy(new Post("An awesome blog post", "Awesomeness is awesome!", PostCategory.NEWS_AND_EVENTS));

		when(post.getId()).thenReturn(123L);

		assertThat(post.getPath(), is("/blog/123-an-awesome-blog-post"));
	}

	@Test
	public void getFormattedPublishDateReturnsSchedulePublishAt() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-11 13:17:01").build();
		assertThat(post.getFormattedPublishDate(), equalTo("June 11, 2013"));
	}

	@Test
	public void getFormattedPublishDateReturnsUnscheduled() throws ParseException {
		Post post = PostBuilder.post().unscheduled().build();
		assertThat(post.getFormattedPublishDate(), equalTo("Unscheduled"));
	}

	@Test
	public void isNotLiveIfDraft() throws ParseException {
		Post post = PostBuilder.post().draft().build();
		assertThat(post.isLiveOn(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2014-06-28 00:00")), is(false));
	}

	@Test
	public void isNotLiveIfScheduledInTheFuture() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-15-12 00:00").build();
		assertThat(post.isLiveOn(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-28 00:00")), is(false));
	}

	@Test
	public void isLiveIfPublishedInThePast() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-01-01 00:00").build();
		assertThat(post.isLiveOn(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-28 00:00")), is(true));
	}

}
