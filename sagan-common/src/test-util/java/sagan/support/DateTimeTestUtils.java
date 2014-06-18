package sagan.support;

import sagan.support.time.DateTimeFactory;
import sagan.support.time.DateTimeUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Date and time related utilities used for testing.
 */
public class DateTimeTestUtils {

    public static final ZoneId TEST_TIME_ZONE = ZoneId.of("Africa/Mogadishu");

    public static Clock createFixedClockForMogadishu(String dateTimeStr) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeUtils.DATE_TIME_FORMATTER_NO_SECONDS);
        return Clock.fixed(Instant.from(dateTime.atZone(TEST_TIME_ZONE)), TEST_TIME_ZONE);
    }

    public static DateTimeFactory createFixedTimeFactory(String dateTimeStr) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeUtils.DATE_TIME_FORMATTER_NO_SECONDS);
        return createFixedTimeFactory(dateTime);
    }

    public static DateTimeFactory createFixedTimeFactory(LocalDateTime dateTime) {
        Clock fixedClock =
                Clock.fixed(Instant.from(dateTime.atZone(DateTimeFactory.DEFAULT_TIME_ZONE)),
                        DateTimeFactory.DEFAULT_TIME_ZONE);
        return DateTimeFactory.from(fixedClock);
    }
}
