package sagan.support;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class DateConverterTests {

    @Test
    public void testConversionToLocalDateTime() {
        String dateFormatString = "2012-07-02 13:42";
        Date startDate = DateTestUtils.getDate(dateFormatString);
        LocalDateTime expectedDate =
                LocalDateTime.parse(dateFormatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        assertEquals(expectedDate, DateConverter.toLocalDateTime(startDate));
    }

    @Test
    public void testConversionToDate() {
        String dateFormatString = "2012-07-02 13:42";
        LocalDateTime startDate =
                LocalDateTime.parse(dateFormatString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Date expectedDate = DateTestUtils.getDate(dateFormatString);
        assertEquals(expectedDate, DateConverter.toDate(startDate));
    }

    @Test
    public void testNullValues() {
        assertNull(DateConverter.toDate(null));
        assertNull(DateConverter.toLocalDateTime(null));
    }
}
