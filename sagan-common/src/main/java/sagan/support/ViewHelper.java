package sagan.support;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Helper class for the views. This is mainly introduced to replace the {@code
 * $ #dates.format(post.publishAt, "MMMM d, yyyy")}} constructs in the templates with
 * their {@link java.time.LocalDate} counter parts (currently no native supported by
 * Thymeleaf).
 */
@Component
public class ViewHelper {

    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter yearMonthFormatter;

    public ViewHelper(Locale locale) {
        Assert.notNull(locale, "'locale' must not be null");
        this.dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", locale);
        this.yearMonthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale);
    }

    public ViewHelper() {
        this(Locale.getDefault());
    }

    public String format(LocalDateTime dateTime) {
        return dateFormatter.format(dateTime);
    }

    public String format(LocalDate date) {
        return dateFormatter.format(date);
    }

    public String format(YearMonth yearMonth) {
        return yearMonthFormatter.format(yearMonth);
    }
}
