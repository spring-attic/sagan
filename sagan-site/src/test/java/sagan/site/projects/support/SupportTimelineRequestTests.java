package sagan.site.projects.support;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SupportTimelineRequest}.
 */
class SupportTimelineRequestTests {

	@Test
	void shouldRejectNullInitialReleaseDate() {
		Assertions.assertThatThrownBy(() -> new SupportTimelineRequest(null))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("initialReleaseDate should not be null");
	}

	@Test
	void shouldRejectInvalidEnforcedOssDate() {
		Assertions.assertThatThrownBy(() -> new SupportTimelineRequest(LocalDate.parse("2021-02-01"), LocalDate.parse("2021-01-01"), null))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("enforcedEndOfOssSupportDate should be after initialReleaseDate");
	}

	@Test
	void shouldRejectInvalidNextGenReleaseDate() {
		Assertions.assertThatThrownBy(() -> new SupportTimelineRequest(LocalDate.parse("2021-02-01"), null, LocalDate.parse("2021-01-01")))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("nextGenReleaseDate should be after initialReleaseDate");
	}

	@Test
	void shouldRejectMissingOssOverlapSupport() {
		Assertions.assertThatThrownBy(() -> new SupportTimelineRequest(LocalDate.parse("2021-01-01"), LocalDate.parse("2021-02-01"), LocalDate.parse("2021-03-01")))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("enforcedEndOfOssSupportDate should be after nextGenReleaseDate");
	}

}