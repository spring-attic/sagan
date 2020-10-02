package sagan.site.projects;


import java.time.LocalDate;
import java.time.Period;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ProjectGeneration}.
 */
public class ProjectGenerationTests {

	@Test
	public void shouldRejectInvalidGenerationNames() {
		assertThatThrownBy(() -> new ProjectGeneration(" ", LocalDate.now()))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Generation name should not be empty");
		assertThatThrownBy(() -> new ProjectGeneration("Something", LocalDate.now()))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Generation name should end with '.x'");
	}

	@Test
	public void shouldRejectNullInitialReleaseDates() {
		assertThatThrownBy(() -> new ProjectGeneration("1.2.x", null))
				.isInstanceOf(InvalidProjectGenerationDateException.class).hasMessage("Initial release date should not be null");
	}

	@Test
	public void shouldEnsureSupportDatesAreAfterRelease() {
		LocalDate twoMonthsAgo = LocalDate.now().minus(Period.ofMonths(2));
		LocalDate aMonthAgo = LocalDate.now().minus(Period.ofMonths(1));

		ProjectGeneration generation = new ProjectGeneration("1.2.x", aMonthAgo);

		assertDateRejectedBecausePriorReleaseDate(() -> generation.setOssSupportPolicyEndDate(twoMonthsAgo));
		assertDateRejectedBecausePriorReleaseDate(() -> generation.setOssSupportEnforcedEndDate(twoMonthsAgo));
		assertDateRejectedBecausePriorReleaseDate(() -> generation.setCommercialSupportPolicyEndDate(twoMonthsAgo));
		assertDateRejectedBecausePriorReleaseDate(() -> generation.setCommercialSupportEnforcedEndDate(twoMonthsAgo));
	}

	@Test
	public void shouldEnsureCommercialSupportEndsAfterOssSupport() {
		LocalDate aMonthAgo = LocalDate.now().minus(Period.ofMonths(1));
		LocalDate twoWeeksAgo = LocalDate.now().minus(Period.ofWeeks(2));
		LocalDate aWeekAgo = LocalDate.now().minus(Period.ofWeeks(1));

		ProjectGeneration generation = new ProjectGeneration("1.2.x", aMonthAgo);
		generation.setOssSupportEnforcedEndDate(aWeekAgo);
		assertThatThrownBy(() -> generation.setCommercialSupportEnforcedEndDate(twoWeeksAgo))
				.isInstanceOf(InvalidProjectGenerationDateException.class).hasMessage("Commercial support should not end before OSS support.");
	}

	@Test
	public void shouldNotSupportUnreleasedGenerations() {
		ProjectGeneration generation = new ProjectGeneration("1.2.x", LocalDate.now().plus(Period.ofMonths(1)));
		assertThat(generation.hasOssSupport()).isFalse();
		assertThat(generation.hasCommercialSupport()).isFalse();
	}

	@Test
	public void shouldEnforceOssSupport() {
		LocalDate aMonthAgo = LocalDate.now().minus(Period.ofMonths(1));
		LocalDate aWeekAgo = LocalDate.now().minus(Period.ofWeeks(1));
		LocalDate nextMonth = LocalDate.now().plus(Period.ofMonths(1));

		ProjectGeneration generation = new ProjectGeneration("1.2.x", aMonthAgo);
		generation.setOssSupportPolicyEndDate(aWeekAgo);
		assertThat(generation.ossSupportEndDate()).isEqualTo(aWeekAgo);
		generation.setOssSupportEnforcedEndDate(nextMonth);
		assertThat(generation.ossSupportEndDate()).isEqualTo(nextMonth);
		assertThat(generation.hasOssSupport()).isTrue();
	}

	@Test
	public void shouldEnforceCommercialSupport() {
		LocalDate aMonthAgo = LocalDate.now().minus(Period.ofMonths(1));
		LocalDate aWeekAgo = LocalDate.now().minus(Period.ofWeeks(1));
		LocalDate nextMonth = LocalDate.now().plus(Period.ofMonths(1));

		ProjectGeneration generation = new ProjectGeneration("1.2.x", aMonthAgo);
		generation.setCommercialSupportPolicyEndDate(aWeekAgo);
		assertThat(generation.commercialSupportEndDate()).isEqualTo(aWeekAgo);
		generation.setCommercialSupportEnforcedEndDate(nextMonth);
		assertThat(generation.commercialSupportEndDate()).isEqualTo(nextMonth);
		assertThat(generation.hasCommercialSupport()).isTrue();
	}

	private void assertDateRejectedBecausePriorReleaseDate(ThrowableAssert.ThrowingCallable callable) {
		assertThatThrownBy(callable).isInstanceOf(InvalidProjectGenerationDateException.class)
				.hasMessage("End of support should be after initial release");
	}
}