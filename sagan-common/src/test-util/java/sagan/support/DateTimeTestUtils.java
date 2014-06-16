package sagan.support;

import sagan.support.time.DateTimeUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Date and time related utilities used for testing.
 */
public class DateTimeTestUtils {

    public static final ZoneId TEST_TIME_ZONE = ZoneId.of("Africa/Mogadishu");

    public static Clock createFixedClockForMogadishu(String dateString) {
        LocalDateTime date = LocalDateTime.parse(dateString, DateTimeUtils.DATE_TIME_FORMATTER_NO_SECONDS);
        return Clock.fixed(Instant.from(date.atZone(TEST_TIME_ZONE)), TEST_TIME_ZONE);
    }
}
