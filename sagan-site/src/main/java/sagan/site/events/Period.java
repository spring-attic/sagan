package sagan.site.events;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.Predicate;

import biweekly.component.VEvent;

import org.springframework.util.Assert;

public class Period {

	private final LocalDate startDate;

	private final int days;

	private Period(LocalDate startDate, int days) {
		this.startDate = startDate;
		this.days = days;
	}

	public static Period of(String startDate, int days) {
		Assert.isTrue(days > 0, "days should be a positive integer");
		return new Period(LocalDate.parse(startDate), days);
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public int getDays() {
		return days;
	}

	protected Predicate<VEvent> toCalendarFilter() {
		return event -> {
			LocalDate eventStart = event.getDateStart().getValue()
					.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			return eventStart.isAfter(this.startDate)
					&& eventStart.isBefore(this.startDate.plusDays(this.days));
		};
	}

	@Override
	public String toString() {
		return "Period{" +
				"startDate=" + startDate +
				", days=" + days +
				'}';
	}
}
