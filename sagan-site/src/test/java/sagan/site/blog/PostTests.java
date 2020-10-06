package sagan.site.blog;

import org.junit.jupiter.api.Disabled;
import sagan.site.support.DateTestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PostTests {
	PostBuilder builder = PostBuilder.post().id(1L);

	@Test
	public void slugReplacesSpacesWithDashes() {
		assertThat(builder.title("This is a title").build().getAdminSlug()).isEqualTo("1-this-is-a-title");
	}

	@Test
	public void slugReplacesMultipleSpacesWithASingleDash() {
		assertThat(builder.title("This    is a title").build().getAdminSlug()).isEqualTo("1-this-is-a-title");
	}

	@Test
	public void slugStripsNonAlphanumericCharacters() {
		assertThat(builder.title("Title 1, with characters';:\\|").build()
				.getAdminSlug()).isEqualTo("1-title-1-with-characters");
	}

	@Test
	public void slugStripsNonAlphanumericCharactersUsedAsDividersWithSpaces() {
		assertThat(builder.title("Title__--1/@something").build().getAdminSlug()).isEqualTo("1-title-1-something");
	}

	@Test
	public void slugStripsNewLineCharacters() {
		assertThat(builder.title("Title 1\n on multiple\nlines").build().getAdminSlug()).isEqualTo("1-title-1-on-multiple-lines");
	}

	@Test
	public void isNotLiveIfDraft() throws ParseException {
		Post post = PostBuilder.post().draft().build();
		assertThat(post.isLiveOn(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2014-06-28 00:00"))).isFalse();
	}

	@Test
	public void isNotLiveIfScheduledInTheFuture() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-15-12 00:00").build();
		assertThat(post.isLiveOn(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-28 00:00"))).isFalse();
	}

	@Disabled("TODO: implement this at some point")
	@Test
	public void isScheduledIfPublishDateIsInTheFuture() throws ParseException {
		Date futureDate = new Date(System.currentTimeMillis() + 10000000);
		Post post = PostBuilder.post().publishAt(futureDate).build();
		assertThat(post.isScheduled()).isTrue();
	}

	@Test
	public void isLiveIfPublishedInThePast() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-01-01 00:00").build();
		assertThat(post.isLiveOn(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-28 00:00"))).isTrue();
	}

	@Test
	public void isLiveIfPublishedNow() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-01-01 00:00").build();
		assertThat(post.isLiveOn(DateTestUtils.getDate("2013-01-01 00:00"))).isTrue();
	}

}
