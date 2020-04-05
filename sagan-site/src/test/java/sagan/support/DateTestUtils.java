package sagan.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTestUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static Date getDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
