package sagan.support;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * A temporary helper class to facilitate a phased migration to the JDK 8 Date/Time API.
 */
public class DateConverter {

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date toDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(date);
        return Date.from(date.toInstant(zoneOffset));
    }
}
