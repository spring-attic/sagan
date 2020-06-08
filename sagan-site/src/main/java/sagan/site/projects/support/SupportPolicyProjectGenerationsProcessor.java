package sagan.site.projects.support;

import java.time.Period;
import java.util.SortedSet;

import sagan.site.projects.ProjectGeneration;

import org.springframework.stereotype.Component;

/**
 * Process a list of {@link ProjectGeneration} and updates support policy dates by looking
 * at the available generations and their initial release dates.
 * 
 * @see <a href="https://tanzu.vmware.com/support/oss">Tanzu official OSS support policy.</a>
 * @see <a href="https://tanzu.vmware.com/support/lifecycle_policy">Tanzu official Commercial support policy</a>
 */
@Component
public class SupportPolicyProjectGenerationsProcessor {

	/**
	 * Open Source Community Project Minor Releases will be supported a minimum of
	 * 12 months from the date such Release was made available.
	 */
	private static Period TWELVE_MONTHS = Period.ofMonths(12);

	/**
	 * Open Source Community Project Major Releases will be supported with an additional
	 * 18 months after the following Major/Minor release was made available.
	 */
	private static Period EIGHTEEN_MONTHS = Period.ofMonths(18);

	/**
	 * Open Source Community Project Major Releases will be supported a minimum of
	 * 18 months from the date such Release was made available.
	 */
	public void updateSupportPolicyDates(SortedSet<ProjectGeneration> generations) {
		ProjectGeneration previousGeneration = null;
		for (ProjectGeneration currentGeneration : generations) {
			currentGeneration.setOssSupportPolicyEndDate(currentGeneration.getInitialReleaseDate().plus(TWELVE_MONTHS));
			if (previousGeneration != null && currentGeneration.isReleased()) {
				previousGeneration.setCommercialSupportPolicyEndDate(currentGeneration.getInitialReleaseDate().plus(EIGHTEEN_MONTHS));
			}
			previousGeneration = currentGeneration;
		}
	}

}
