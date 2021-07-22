package sagan.site.projects.support;

import java.time.LocalDate;
import java.time.Period;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Tests for {@link SupportPolicy}
 */
public class SupportPolicyTests {

	@Test
	void shouldRejectNullReleaseDate() {
		Assertions.assertThatThrownBy(() -> SupportPolicy.SPRING_BOOT.calculateTimeline(null, null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@MethodSource("ossSupportProvider")
	void shouldCalculateOssSupportDateWithoutNextRelease(SupportPolicy policy, LocalDate currentGenReleaseDate, LocalDate expectedEndSupportDate) {
		SupportTimeline supportTimeline = policy.calculateTimeline(currentGenReleaseDate, null);
		assertThat(supportTimeline.getOpenSourceSupport().getStartDate()).isEqualTo(currentGenReleaseDate);
		assertThat(supportTimeline.getOpenSourceSupport().getEndDate()).isEqualTo(expectedEndSupportDate);
		assertThat(supportTimeline.getOpenSourceSupport().getReason()).isEqualTo("P12M OSS support with " + policy.getLabel() + " support policy.");
	}

	static Stream<Arguments> ossSupportProvider() {
		LocalDate releaseDate = LocalDate.parse("2021-01-15");
		LocalDate twelveMonthsLater = releaseDate.plus(Period.ofMonths(12));
		return Stream.of(
				arguments(SupportPolicy.UPSTREAM, releaseDate, twelveMonthsLater),
				arguments(SupportPolicy.SPRING_BOOT, releaseDate, twelveMonthsLater),
				arguments(SupportPolicy.DOWNSTREAM, releaseDate, twelveMonthsLater)
		);
	}

	@ParameterizedTest
	@MethodSource("commercialSupportProvider")
	void shouldCalculateCommercialSupportDateWithoutNextRelease(SupportPolicy policy, LocalDate currentGenReleaseDate, LocalDate expectedEndSupportDate) {
		SupportTimeline supportTimeline = policy.calculateTimeline(currentGenReleaseDate, null);
		assertThat(supportTimeline.getCommercialSupport().getStartDate()).isEqualTo(supportTimeline.getOpenSourceSupport().getEndDate());
		assertThat(supportTimeline.getCommercialSupport().getEndDate()).isEqualTo(expectedEndSupportDate);
		assertThat(supportTimeline.getCommercialSupport().getReason()).contains("commercial support with " + policy.getLabel() + " support policy.");
	}

	static Stream<Arguments> commercialSupportProvider() {
		LocalDate releaseDate = LocalDate.parse("2021-01-15");
		return Stream.of(
				arguments(SupportPolicy.UPSTREAM, releaseDate, releaseDate.plus(Period.ofMonths(28))),
				arguments(SupportPolicy.SPRING_BOOT, releaseDate, releaseDate.plus(Period.ofMonths(27))),
				arguments(SupportPolicy.DOWNSTREAM, releaseDate, releaseDate.plus(Period.ofMonths(24)))
		);
	}

	@ParameterizedTest
	@MethodSource("upgradeCommercialSupportProvider")
	void shouldCalculateCommercialSupportDateWithSupportPolicy(SupportPolicy policy, LocalDate currentGenReleaseDate, LocalDate nextGenReleaseDate, LocalDate expectedEndSupportDate) {
		SupportTimeline supportTimeline = policy.calculateTimeline(currentGenReleaseDate, nextGenReleaseDate);
		assertThat(supportTimeline.getCommercialSupport().getStartDate()).isEqualTo(supportTimeline.getOpenSourceSupport().getEndDate());
		assertThat(supportTimeline.getCommercialSupport().getEndDate()).isEqualTo(expectedEndSupportDate);
		assertThat(supportTimeline.getCommercialSupport().getReason()).isEqualTo("P12M of upgrade support (next generation released on 2023-01-15).");
	}

	static Stream<Arguments> upgradeCommercialSupportProvider() {
		LocalDate releaseDate = LocalDate.parse("2021-01-15");
		LocalDate nextGenReleaseDate = releaseDate.plus(Period.ofMonths(24));
		LocalDate upgradePolicyDate = nextGenReleaseDate.plus(Period.ofMonths(12));

		return Stream.of(
				arguments(SupportPolicy.UPSTREAM, releaseDate, nextGenReleaseDate, upgradePolicyDate),
				arguments(SupportPolicy.SPRING_BOOT, releaseDate, nextGenReleaseDate, upgradePolicyDate),
				arguments(SupportPolicy.DOWNSTREAM, releaseDate, nextGenReleaseDate, upgradePolicyDate)
		);
	}

}
