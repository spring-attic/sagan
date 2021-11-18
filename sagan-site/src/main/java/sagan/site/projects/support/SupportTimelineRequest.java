package sagan.site.projects.support;

import java.time.LocalDate;

import org.springframework.util.Assert;

/**
 * Request object that holds dates related to a project generation, required for
 * calculating a {@link SupportTimeline}.
 */
public class SupportTimelineRequest {

	private final LocalDate initialReleaseDate;

	private final LocalDate enforcedEndOfOssSupportDate;

	private final LocalDate nextGenReleaseDate;

	public SupportTimelineRequest (LocalDate initialReleaseDate) {
		this(initialReleaseDate, null, null);
	}

	/**
	 * Create a SupportTimelineRequest to be calculated by a {@link SupportPolicy}.
	 * 
	 * @param initialReleaseDate release date of the considered generation
	 * @param enforcedEndOfOssSupportDate end of OSS support date enforced by the project maintainer, can be null
	 * @param nextGenReleaseDate release date of the next generation, can be null
	 */
	public SupportTimelineRequest(LocalDate initialReleaseDate, LocalDate enforcedEndOfOssSupportDate, LocalDate nextGenReleaseDate) {
		Assert.notNull(initialReleaseDate, "initialReleaseDate should not be null");
		if (enforcedEndOfOssSupportDate != null) {
			Assert.isTrue(initialReleaseDate.isBefore(enforcedEndOfOssSupportDate), "enforcedEndOfOssSupportDate should be after initialReleaseDate");
		}
		if (nextGenReleaseDate != null) {
			Assert.isTrue(initialReleaseDate.isBefore(nextGenReleaseDate), "nextGenReleaseDate should be after initialReleaseDate");
		}
		if (enforcedEndOfOssSupportDate != null && nextGenReleaseDate != null) {
			Assert.isTrue(enforcedEndOfOssSupportDate.isAfter(nextGenReleaseDate), "enforcedEndOfOssSupportDate should be after nextGenReleaseDate");
		}
		this.initialReleaseDate = initialReleaseDate;
		this.enforcedEndOfOssSupportDate = enforcedEndOfOssSupportDate;
		this.nextGenReleaseDate = nextGenReleaseDate;
	}

	public LocalDate getInitialReleaseDate() {
		return this.initialReleaseDate;
	}

	public LocalDate getEnforcedEndOfOssSupportDate() {
		return this.enforcedEndOfOssSupportDate;
	}

	public LocalDate getNextGenReleaseDate() {
		return this.nextGenReleaseDate;
	}
}
