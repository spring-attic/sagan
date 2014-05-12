package sagan.support;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

@Component
public class DateFactory {

    public static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("UTC");

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public ZoneId timeZone() {
        return DEFAULT_TIME_ZONE;
    }
}
