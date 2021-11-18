package sagan.site.projects.support;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.util.Assert;

/**
 * Support policy for Spring projects.
 *
 * @see <a href="https://tanzu.vmware.com/support/oss">Tanzu official OSS support policy.</a>
 * @see <a href="https://tanzu.vmware.com/support/lifecycle_policy">Tanzu official Commercial support policy</a>
 */
public enum SupportPolicy {

	/**
	 * Support policy for Spring projects that Spring Boot depends on.
	 * A generation must be supported for at least the lifetime of the Spring Boot release.
	 * The minimum support policy is set to 28 months (12 months OSS, 16 commercial).
	 */
	UPSTREAM("Upstream", Period.ofMonths(12), Period.ofMonths(16)),

	/**
	 * Support policy for Spring Boot.
	 * The minimum support policy is set to 27 months (12 months OSS, 15 commercial).
	 */
	SPRING_BOOT("Spring Boot", Period.ofMonths(12), Period.ofMonths(15)),

	/**
	 * Support policy for downstream Spring projects.
	 * The minimum support policy is set to 24 months (12 months OSS, 12 commercial).
	 * Downstream projects must release within 3 months of Spring Boot
	 * or wait for the next release in order to make this policy possible.
	 */
	DOWNSTREAM("Downstream", Period.ofMonths(12), Period.ofMonths(12));

	private final String label;

	private final Period openSourceSupport;

	private final Period commercialSupport;

	/**
	 * Project must provide at least 12 months of support for previous versions.
	 * This means that when a new minor version is released, the previous one must be supported
	 * for at least 12 months after that.
	 */
	private final Period upgradeSupport = Period.ofMonths(12);

	SupportPolicy(String label, Period openSourceSupport, Period commercialSupport) {
		this.label = label;
		this.openSourceSupport = openSourceSupport;
		this.commercialSupport = commercialSupport;
	}

	public String getLabel() {
		return this.label;
	}

	/**
	 * Calculate the support timeline with the given release dates.
	 *
	 * @param supportTimelineRequest@return the support timeline for the current {@link SupportPolicy}.
	 */
	public SupportTimeline calculateTimeline(SupportTimelineRequest supportTimelineRequest) {
		Assert.notNull(supportTimelineRequest, "supportTimelineRequest is required.");
		SupportTimeline.Builder builder = SupportTimeline.create(supportTimelineRequest.getInitialReleaseDate());

		LocalDate openSourceSupportEndDate = supportTimelineRequest.getInitialReleaseDate().plus(this.openSourceSupport);
		LocalDate enforcedEndOfOssSupportDate = supportTimelineRequest.getEnforcedEndOfOssSupportDate();
		if (enforcedEndOfOssSupportDate != null) {
			builder.openSourceSupport(supportTimelineRequest.getInitialReleaseDate(), enforcedEndOfOssSupportDate,
					this.openSourceSupport + " OSS support with enforced date, " + this.getLabel() + " support policy is '" + enforcedEndOfOssSupportDate + "'.");
		}
		else {
			builder.openSourceSupport(supportTimelineRequest.getInitialReleaseDate(), openSourceSupportEndDate,
					this.openSourceSupport + " OSS support with " + this.getLabel() + " support policy.");
		}

		LocalDate actualOpenSourceSupportEndDate = enforcedEndOfOssSupportDate != null ? enforcedEndOfOssSupportDate : openSourceSupportEndDate;
		LocalDate commercialSupportEndDate = actualOpenSourceSupportEndDate.plus(this.commercialSupport);
		if (supportTimelineRequest.getNextGenReleaseDate() != null) {
			LocalDate upgradeSupportEndDate = supportTimelineRequest.getNextGenReleaseDate().plus(this.upgradeSupport);
			if (upgradeSupportEndDate.isAfter(commercialSupportEndDate)) {
				builder.commercialSupport(actualOpenSourceSupportEndDate, upgradeSupportEndDate,
						this.upgradeSupport + " of upgrade support (next generation released on " + supportTimelineRequest.getNextGenReleaseDate() + ").");
			}
			else {
				builder.commercialSupport(actualOpenSourceSupportEndDate, commercialSupportEndDate,
						this.commercialSupport + " commercial support with " + this.getLabel() + " support policy.");
			}
		}
		else {
			builder.commercialSupport(actualOpenSourceSupportEndDate, commercialSupportEndDate,
					this.commercialSupport + " commercial support with " + this.getLabel() + " support policy.");
		}
		return builder.build();
	}

}
