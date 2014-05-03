package sagan.blog;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class PostTests {
    PostBuilder builder = PostBuilder.post().id(1L);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Test
    public void slugReplacesSpacesWithDashes() {
        assertEquals("1-this-is-a-title", builder.title("This is a title").build().getAdminSlug());
    }

    @Test
    public void slugReplacesMultipleSpacesWithASingleDash() {
        assertEquals("1-this-is-a-title", builder.title("This    is a title").build().getAdminSlug());
    }

    @Test
    public void slugStripsNonAlphanumericCharacters() {
        assertEquals("1-title-1-with-characters", builder.title("Title 1, with characters';:\\|").build()
                .getAdminSlug());
    }

    @Test
    public void slugStripsNonAlphanumericCharactersUsedAsDividersWithSpaces() {
        assertEquals("1-title-1-something", builder.title("Title__--1/@something").build().getAdminSlug());
    }

    @Test
    public void slugStripsNewLineCharacters() {
        assertEquals("1-title-1-on-multiple-lines", builder.title("Title 1\n on multiple\nlines").build()
                .getAdminSlug());
    }

    @Test
    public void isNotLiveIfDraft() throws ParseException {
        Post post = PostBuilder.post().draft().build();
        assertThat(post.isLiveOn(LocalDateTime.parse("2014-06-28 00:00", dateTimeFormatter)), is(false));
    }

    @Test
    public void isNotLiveIfScheduledInTheFuture() throws ParseException {
        Post post = PostBuilder.post().publishAt("2013-12-15 00:00").build();
        assertThat(post.isLiveOn(LocalDateTime.parse("2013-06-28 00:00", dateTimeFormatter)), is(false));
    }

    @Ignore("TODO: implement this at some point")
    @Test
    public void isScheduledIfPublishDateIsInTheFuture() throws ParseException {
        LocalDateTime futureDate = LocalDateTime.now().plusHours(2);
        Post post = PostBuilder.post().publishAt(futureDate).build();
        assertThat(post.isScheduled(), is(true));
    }

    @Test
    public void isLiveIfPublishedInThePast() throws ParseException {
        Post post = PostBuilder.post().publishAt("2013-01-01 00:00").build();
        assertThat(post.isLiveOn(LocalDateTime.parse("2013-06-28 00:00", dateTimeFormatter)), is(true));
    }

    @Test
    public void isLiveIfPublishedNow() throws ParseException {
        Post post = PostBuilder.post().publishAt("2013-01-01 00:00").build();
        assertThat(post.isLiveOn(LocalDateTime.parse("2013-01-01 00:00", dateTimeFormatter)), is(true));
    }

}
