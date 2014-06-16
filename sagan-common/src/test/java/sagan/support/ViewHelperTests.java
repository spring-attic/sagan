package sagan.support;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Tests for {@link sagan.support.ViewHelper}.
 */
public class ViewHelperTests {

    // Sagan is currently based on the default locale, set it to US for this test
    private ViewHelper viewHelper = new ViewHelper(Locale.US);

    @Test
    public void formatDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2014, 6, 24, 10, 20);
        String actual = viewHelper.format(dateTime);
        assertThat(actual, is("June 24, 2014"));
    }

    @Test
    public void formatDate() {
        LocalDate date = LocalDate.of(2014, 6, 13);
        String actual = viewHelper.format(date);
        assertThat(actual, is("June 13, 2014"));
    }

    @Test
    public void formatYearMonth() {
        YearMonth yearMonth = YearMonth.of(2014, 6);
        String actual = viewHelper.format(yearMonth);
        assertThat(actual, is("June 2014"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void noLocale() {
        new ViewHelper(null);
    }
}