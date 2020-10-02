package sagan.site.projects.support;

import java.time.LocalDate;
import java.time.Period;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import sagan.site.projects.ProjectGeneration;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for {@link SupportPolicyProjectGenerationsProcessor}
 */
public class SupportPolicyProjectGenerationsProcessorTests {

	private final SupportPolicyProjectGenerationsProcessor processor = new SupportPolicyProjectGenerationsProcessor();

	@Test
	public void shouldUpdateOssSupportPolicy() {
		TreeSet<ProjectGeneration> generations = new TreeSet<>();
		ProjectGeneration twoOneX = new ProjectGeneration("2.1.x", LocalDate.parse("2020-01-15"));
		generations.add(twoOneX);
		this.processor.updateSupportPolicyDates(generations);

		assertThat(twoOneX.getOssSupportPolicyEndDate()).isEqualTo(LocalDate.parse("2021-01-15"));
		assertThat(twoOneX.getCommercialSupportPolicyEndDate()).isNull();
	}

	@Test
	public void shouldUpdateCommercialSupportPolicy() {
		TreeSet<ProjectGeneration> generations = new TreeSet<>();
		ProjectGeneration twoOneX = new ProjectGeneration("2.1.x", LocalDate.parse("2019-01-15"));
		ProjectGeneration twoTwoX = new ProjectGeneration("2.2.x", LocalDate.parse("2019-07-02"));
		generations.add(twoOneX);
		generations.add(twoTwoX);
		this.processor.updateSupportPolicyDates(generations);

		assertThat(twoOneX.getOssSupportPolicyEndDate()).isEqualTo(LocalDate.parse("2020-01-15"));
		assertThat(twoOneX.getCommercialSupportPolicyEndDate()).isEqualTo(LocalDate.parse("2021-01-02"));
		assertThat(twoTwoX.getOssSupportPolicyEndDate()).isEqualTo(LocalDate.parse("2020-07-02"));
		assertThat(twoTwoX.getCommercialSupportPolicyEndDate()).isNull();
	}

	@Test
	public void shouldNotUpdateCommercialSupportPolicyIfNotReleased() {
		TreeSet<ProjectGeneration> generations = new TreeSet<>();
		ProjectGeneration twoOneX = new ProjectGeneration("2.1.x", LocalDate.parse("2020-01-15"));
		ProjectGeneration twoTwoX = new ProjectGeneration("2.2.x", LocalDate.now().plus(Period.ofMonths(1)));
		generations.add(twoOneX);
		generations.add(twoTwoX);
		this.processor.updateSupportPolicyDates(generations);

		assertThat(twoOneX.getOssSupportPolicyEndDate()).isEqualTo(LocalDate.parse("2021-01-15"));
		assertThat(twoOneX.getCommercialSupportPolicyEndDate()).isNull();
		assertThat(twoTwoX.getOssSupportPolicyEndDate()).isEqualTo(LocalDate.now().plus(Period.ofMonths(13)));
		assertThat(twoTwoX.getCommercialSupportPolicyEndDate()).isNull();
	}

}