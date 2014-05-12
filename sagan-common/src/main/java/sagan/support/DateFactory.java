package sagan.support;

import java.time.LocalDateTime;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

@Component
public class DateFactory {

    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public TimeZone timeZone() {
        return DEFAULT_TIME_ZONE;
    }
}
