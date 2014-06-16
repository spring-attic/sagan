package sagan.support.time;

import org.junit.Test;
import sagan.support.DateTimeTestUtils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


/**
 * Tests for {@link sagan.support.time.DateTimeFactory}.
 */
public class DateTimeFactoryTests {

    private Clock fixedClock = DateTimeTestUtils.createFixedClockForMogadishu("2014-12-24 14:30");

    @Test(expected = IllegalArgumentException.class)
    public void noClock() {
        DateTimeFactory.from(null);
    }

    @Test
    public void timeZone() {
        assertThat(DateTimeFactory.from(fixedClock).timeZone(), is(fixedClock.getZone()));
        assertThat(DateTimeFactory.withDefaultTimeZone().timeZone(), is(DateTimeFactory.DEFAULT_TIME_ZONE));
    }

    @Test
    public void clock() {
        assertThat(DateTimeFactory.from(fixedClock).clock(), is(fixedClock));
        assertThat(DateTimeFactory.withDefaultTimeZone().clock(), is(Clock.system(DateTimeFactory.DEFAULT_TIME_ZONE)));
    }

    @Test
    public void now() throws Exception {
        assertThat(DateTimeFactory.from(fixedClock).now(), is(LocalDateTime.now(fixedClock)));
    }

    @Test
    public void zonedNow() throws Exception {
        assertThat(DateTimeFactory.from(fixedClock).zonedNow(), is(ZonedDateTime.now(fixedClock)));
    }

    @Test
    public void date() {
        assertThat(DateTimeFactory.from(fixedClock).date(), is(LocalDate.of(2014, 12, 24)));

        // this could in theory fail if run at midnight ... but that is like winning in lottery ... ten times in a row.
        assertThat(DateTimeFactory.withDefaultTimeZone().date(), is(LocalDateTime.now(Clock.system(DateTimeFactory.DEFAULT_TIME_ZONE)).toLocalDate()));
    }
}