package sagan.support.time;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.*;

/**
 * Factory for creating {@link LocalDateTime}s and other date and time related objects.
 * <p>
 * <ul>
 * <li>If not supplied with a {@link Clock}, one will be created with the UTC time-zone.</li>
 * <li>Test classes will typically want to provide a {@link Clock#fixed(java.time.Instant, java.time.ZoneId) fixed clock}
 * or a {@link Clock#tick(java.time.Clock, java.time.Duration) ticking clock} starting at a fixed point in time.</li>
 * </ul>
 *
 * @see Clock#fixed(java.time.Instant, java.time.ZoneId)
 * @see Clock#tick(java.time.Clock, java.time.Duration)
 */
@Component
public class DateTimeFactory {

    public static final ZoneOffset DEFAULT_TIME_ZONE = ZoneOffset.UTC;

    private final Clock clock;

    public DateTimeFactory() {
        this(Clock.system(DEFAULT_TIME_ZONE));
    }

    private DateTimeFactory(Clock clock) {
        Assert.notNull(clock, "'clock' must not be null");
        this.clock = clock;
    }

    /**
     * Create a {@link DateTimeFactory} with the default time-zone and a system clock.
     */
    public static DateTimeFactory withDefaultTimeZone() {
        return new DateTimeFactory();
    }

    /**
     * Create a {@link DateTimeFactory} with the supplied {@link Clock}.
     *
     * @param clock the {@link Clock} to use, not null
     */
    public static DateTimeFactory from(Clock clock) {
        return new DateTimeFactory(clock);
    }

    /**
     * Get the {@link ZoneId time-zone} used by this factory.
     */
    public ZoneId timeZone() {
        return clock().getZone();
    }

    /**
     * Get the {@link Clock clock} used by this factory.
     */
    public Clock clock() {
        return clock;
    }

    /**
     * Get the current {@link LocalDateTime} given the underlying {@link #clock()}.
     */
    public LocalDateTime now() {
        return LocalDateTime.now(clock());
    }

    /**
     * Get the current {@link ZonedDateTime} given the underlying {@link #clock()}.
     */
    public ZonedDateTime zonedNow() {
        return ZonedDateTime.now(clock);
    }

    /**
     * Get the current {@link LocalDate} given the underlying {@link #clock()}.
     */
    public LocalDate date() {
        return now().toLocalDate();
    }
}
