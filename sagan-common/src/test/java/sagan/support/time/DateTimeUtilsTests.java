package sagan.support.time;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


/**
 * Tests for {@link sagan.support.time.DateTimeUtils}.
 */
public class DateTimeUtilsTests {

    @Test
    public void parseValidDateTime() {
        LocalDateTime expected = LocalDateTime.of(2014, 12, 24, 14, 30);
        LocalDateTime actual = DateTimeUtils.parseDateTimeNoSeconds("2014-12-24 14:30");

        assertThat(actual, is(expected));
    }

    @Test
    public void epoch() {
        LocalDateTime expected = DateTimeUtils.parseDateTimeNoSeconds("1970-01-01 00:00");
        LocalDateTime actual = DateTimeUtils.epoch();

        assertThat(actual, is(expected));
    }

    @Test
    public void toDate() {
        LocalDateTime expected = DateTimeUtils.parseDateTimeNoSeconds("2014-12-24 14:30");
        Date actual = DateTimeUtils.toDate(expected);

        assertThat(actual.getTime(), is(expected.toInstant(DateTimeFactory.DEFAULT_TIME_ZONE).toEpochMilli()));
        assertThat(DateTimeUtils.toDate(null), is(nullValue()));
    }

    @Test
    public void formatAsDate() {
        LocalDateTime dateTime = DateTimeUtils.parseDateTimeNoSeconds("2014-12-24 23:45");
        String actual = DateTimeUtils.formatAsDate(dateTime.atZone(ZoneId.systemDefault()));

        assertThat(actual, is("2014-12-24"));
    }
}