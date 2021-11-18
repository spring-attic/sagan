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
		Assertions.assertThatThrownBy(() -> SupportPolicy.SPRING_BOOT.calculateTimeline(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@MethodSource("ossSupportProvider")
	void shouldCalculateOssSupportDateWithoutNextRelease(SupportPolicy policy, SupportTimelineRequest request, LocalDate expectedEndSupportDate) {
		SupportTimeline supportTimeline = policy.calculateTimeline(request);
		assertThat(supportTimeline.getOpenSourceSupport().getStartDate()).isEqualTo(request.getInitialReleaseDate());
		assertThat(supportTimeline.getOpenSourceSupport().getEndDate()).isEqualTo(expectedEndSupportDate);
		assertThat(supportTimeline.getOpenSourceSupport().getReason()).isEqualTo("P12M OSS support with " + policy.getLabel() + " support policy.");
	}

	static Stream<Arguments> ossSupportProvider() {
		LocalDate releaseDate = LocalDate.parse("2021-01-15");
		SupportTimelineRequest request = new SupportTimelineRequest(releaseDate);
		LocalDate twelveMonthsLater = releaseDate.plus(Period.ofMonths(12));
		return Stream.of(
				arguments(SupportPolicy.UPSTREAM, request, twelveMonthsLater),
				arguments(SupportPolicy.SPRING_BOOT, request, twelveMonthsLater),
				arguments(SupportPolicy.DOWNSTREAM, request, twelveMonthsLater)
		);
	}

	@ParameterizedTest
	@MethodSource("commercialSupportProvider")
	void shouldCalculateCommercialSupportDateWithoutNextRelease(SupportPolicy policy, SupportTimelineRequest request, LocalDate expectedEndSupportDate) {
		SupportTimeline supportTimeline = policy.calculateTimeline(request);
		assertThat(supportTimeline.getCommercialSupport().getStartDate()).isEqualTo(supportTimeline.getOpenSourceSupport().getEndDate());
		assertThat(supportTimeline.getCommercialSupport().getEndDate()).isEqualTo(expectedEndSupportDate);
		assertThat(supportTimeline.getCommercialSupport().getReason()).contains("commercial support with " + policy.getLabel() + " support policy.");
	}

	static Stream<Arguments> commercialSupportProvider() {
		LocalDate releaseDate = LocalDate.parse("2021-01-15");
		SupportTimelineRequest request = new SupportTimelineRequest(releaseDate);
		return Stream.of(
				arguments(SupportPolicy.UPSTREAM, request, releaseDate.plus(Period.ofMonths(28))),
				arguments(SupportPolicy.SPRING_BOOT, request, releaseDate.plus(Period.ofMonths(27))),
				arguments(SupportPolicy.DOWNSTREAM, request, releaseDate.plus(Period.ofMonths(24)))
		);
	}

	@ParameterizedTest
	@MethodSource("upgradeCommercialSupportProvider")
	void shouldCalculateCommercialSupportDateWithSupportPolicy(SupportPolicy policy, SupportTimelineRequest request, LocalDate expectedEndSupportDate) {
		SupportTimeline supportTimeline = policy.calculateTimeline(request);
		assertThat(supportTimeline.getCommercialSupport().getStartDate()).isEqualTo(supportTimeline.getOpenSourceSupport().getEndDate());
		assertThat(supportTimeline.getCommercialSupport().getEndDate()).isEqualTo(expectedEndSupportDate);
		assertThat(supportTimeline.getCommercialSupport().getReason()).isEqualTo("P12M of upgrade support (next generation released on 2023-01-15).");
	}

	static Stream<Arguments> upgradeCommercialSupportProvider() {
		LocalDate releaseDate = LocalDate.parse("2021-01-15");
		LocalDate nextGenReleaseDate = releaseDate.plus(Period.ofMonths(24));
		SupportTimelineRequest request = new SupportTimelineRequest(releaseDate, null, nextGenReleaseDate);
		LocalDate upgradePolicyDate = nextGenReleaseDate.plus(Period.ofMonths(12));
		return Stream.of(
				arguments(SupportPolicy.UPSTREAM, request, upgradePolicyDate),
				arguments(SupportPolicy.SPRING_BOOT, request, upgradePolicyDate),
				arguments(SupportPolicy.DOWNSTREAM, request, upgradePolicyDate)
		);
	}

	@ParameterizedTest
	@MethodSource("enforcedOssSupportProvider")
	void shouldCalculateCommercialSupportDateWithEnforcedOssDate(SupportPolicy policy, SupportTimelineRequest request, LocalDate expectedEndSupportDate) {
		SupportTimeline supportTimeline = policy.calculateTimeline(request);
		assertThat(supportTimeline.getOpenSourceSupport().getStartDate()).isEqualTo(request.getInitialReleaseDate());
		assertThat(supportTimeline.getOpenSourceSupport().getEndDate()).isEqualTo(request.getEnforcedEndOfOssSupportDate());
		assertThat(supportTimeline.getOpenSourceSupport().getReason())
				.isEqualTo("P12M OSS support with enforced date, " + policy.getLabel() + " support policy is '" + request.getEnforcedEndOfOssSupportDate() + "'.");
		assertThat(supportTimeline.getCommercialSupport().getStartDate()).isEqualTo(supportTimeline.getOpenSourceSupport().getEndDate());
		assertThat(supportTimeline.getCommercialSupport().getEndDate()).isEqualTo(expectedEndSupportDate);
		assertThat(supportTimeline.getCommercialSupport().getReason()).contains("commercial support with " + policy.getLabel() + " support policy.");
	}

	static Stream<Arguments> enforcedOssSupportProvider() {
		LocalDate releaseDate = LocalDate.parse("2021-01-15");
		LocalDate enforcedOssSupportEndDate = releaseDate.plus(Period.ofMonths(15));
		SupportTimelineRequest request = new SupportTimelineRequest(releaseDate, enforcedOssSupportEndDate, null);
		return Stream.of(
				arguments(SupportPolicy.UPSTREAM, request, enforcedOssSupportEndDate.plus(Period.ofMonths(16))),
				arguments(SupportPolicy.SPRING_BOOT, request, enforcedOssSupportEndDate.plus(Period.ofMonths(15))),
				arguments(SupportPolicy.DOWNSTREAM, request, enforcedOssSupportEndDate.plus(Period.ofMonths(12)))
		);
	}

}
