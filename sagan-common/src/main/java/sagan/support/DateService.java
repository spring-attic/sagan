package sagan.support;

import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

@Service
public class DateService {
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

    public Date now() {
        return new Date();
    }
}
