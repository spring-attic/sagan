package sagan.site.projects.support;

import java.time.LocalDate;

/**
 * A support period is a piece of a {@link SupportTimeline} that has start and end dates,
 * but also a reason that explains the rationale for its duration, which depends on the {@link SupportPolicy}.
 */
public class SupportPeriod {

	private final LocalDate startDate;

	private final LocalDate endDate;

	private final String reason;

	SupportPeriod(LocalDate startDate, LocalDate endDate, String reason) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.reason = reason;
	}

	public LocalDate getStartDate() {
		return this.startDate;
	}

	public LocalDate getEndDate() {
		return this.endDate;
	}

	public String getReason() {
		return this.reason;
	}

	@Override
	public String toString() {
		return "SupportPeriod{" +
				"startDate=" + startDate +
				", endDate=" + endDate +
				", reason='" + reason + '\'' +
				'}';
	}
}
