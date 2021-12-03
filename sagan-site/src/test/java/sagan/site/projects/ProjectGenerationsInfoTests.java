package sagan.site.projects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import sagan.site.projects.support.SupportPolicy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectGenerationsInfo}.
 */
class ProjectGenerationsInfoTests {

	private final ProjectGeneration oneDotZero = new ProjectGeneration("1.0.x", LocalDate.of(2020,1,1));

	private final ProjectGeneration oneDotOne = new ProjectGeneration("1.1.x", LocalDate.of(2021,1,1));

	private final ProjectGeneration oneDotTwo = new ProjectGeneration("1.2.x", LocalDate.of(2022,1,1));

	private final SortedSet<ProjectGeneration> generations = new TreeSet<>(Arrays.asList(oneDotZero, oneDotOne, oneDotTwo));

	@Test
	void shouldComputeSupportDatesForAllGenerations() {
		ProjectGenerationsInfo generationsInfo = new ProjectGenerationsInfo();
		generationsInfo.setGenerations(this.generations);
		generationsInfo.computeSupportPolicyDates(SupportPolicy.SPRING_BOOT);

		assertThat(generationsInfo.getGenerations())
				.allMatch(generation -> generation.getOssSupportPolicyEndDate() != null)
				.allMatch(generation -> generation.getCommercialSupportPolicyEndDate() != null);
	}

	@Test
	void shouldRecordModificationDate() {
		OffsetDateTime before = LocalDateTime.now().atOffset(ZoneOffset.UTC);
		ProjectGenerationsInfo generationsInfo = new ProjectGenerationsInfo();
		generationsInfo.setGenerations(this.generations);
		generationsInfo.computeSupportPolicyDates(SupportPolicy.SPRING_BOOT);
		assertThat(generationsInfo.getLastModified()).isAfter(before);
	}

}