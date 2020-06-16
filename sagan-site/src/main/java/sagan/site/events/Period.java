package sagan.site.events;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Predicate;

import org.springframework.util.Assert;

public class Period {

	private final ZonedDateTime startDateTime;

	private final int days;

	private Period(ZonedDateTime startDateTime, int days) {
		this.startDateTime = startDateTime;
		this.days = days;
	}

	public static Period of(String startDate, int days) {
		Assert.isTrue(days > 0, "days should be a positive integer");
		ZonedDateTime startDateTime = ZonedDateTime.of(LocalDate.parse(startDate), LocalTime.MIDNIGHT, ZoneId.systemDefault());
		return new Period(startDateTime, days);
	}

	public ZonedDateTime getStartDateTime() {
		return this.startDateTime;
	}

	public int getDays() {
		return days;
	}

	protected Predicate<Event> toCalendarFilter() {
		return event -> {
			return event.getStartTime().isAfter(this.startDateTime)
					&& event.getStartTime().isBefore(this.startDateTime.plusDays(this.days));
		};
	}

	@Override
	public String toString() {
		return "Period{" +
				"startDateTime=" + startDateTime +
				", days=" + days +
				'}';
	}
}
