package sagan.support.time;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utility class for {@link java.time.LocalDate date} and {@link java.time.LocalDateTime time} related methods.
 */
public class DateTimeUtils {

    public static final DateTimeFormatter DATE_TIME_FORMATTER_NO_SECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateTimeUtils() {
    }

    /**
     * Parse the {@code dateTimeStr} with format {@code yyyy-MM-dd HH:mm} to a {@link LocalDateTime}
     */
    public static LocalDateTime parseDateTimeNoSeconds(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER_NO_SECONDS);
    }

    /**
     * Format the given {@link ZonedDateTime} on the form {@code yyyy-MM-dd}
     */
    public static String formatAsDate(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DATE_FORMATTER);
    }

    /**
     * Get the {@link LocalDateTime} for {@code 1970-01-01T00:00:00Z }
     */
    public static LocalDateTime epoch() {
        return LocalDateTime.ofInstant(Instant.EPOCH, DateTimeFactory.DEFAULT_TIME_ZONE);
    }

    /**
     * Convert the {@link LocalDateTime} to a {@link Date}
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if(localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.toInstant(DateTimeFactory.DEFAULT_TIME_ZONE));
    }
}
