package sagan.site.projects.support;

import java.time.LocalDate;

import org.springframework.util.Assert;

/**
 * The support timeline for a {@link sagan.site.projects.ProjectGeneration}, given a defined {@link SupportPolicy}.
 * A timeline is made of two {@link SupportPeriod}: open source support and commercial support.
 */
public class SupportTimeline {

	private final LocalDate initialRelease;

	private final SupportPeriod openSourceSupport;

	private final SupportPeriod commercialSupport;

	SupportTimeline(LocalDate initialRelease, SupportPeriod openSourceSupport, SupportPeriod commercialSupport) {
		this.initialRelease = initialRelease;
		this.openSourceSupport = openSourceSupport;
		this.commercialSupport = commercialSupport;
	}

	public static Builder create(LocalDate initialRelease) {
		return new Builder(initialRelease);
	}

	public LocalDate getInitialRelease() {
		return this.initialRelease;
	}

	public SupportPeriod getOpenSourceSupport() {
		return this.openSourceSupport;
	}

	public SupportPeriod getCommercialSupport() {
		return this.commercialSupport;
	}

	@Override
	public String toString() {
		return "SupportTimeline{" +
				"initialRelease=" + initialRelease +
				", openSourceSupport=" + openSourceSupport +
				", commercialSupport=" + commercialSupport +
				'}';
	}

	public static class Builder {

		private final LocalDate initialRelease;

		private SupportPeriod openSourceSupport;

		private SupportPeriod commercialSupport;

		Builder(LocalDate initialRelease) {
			this.initialRelease = initialRelease;
		}

		public Builder openSourceSupport(LocalDate startDate, LocalDate endDate, String reason) {
			this.openSourceSupport = new SupportPeriod(startDate, endDate, reason);
			return this;
		}

		public Builder commercialSupport(LocalDate startDate, LocalDate endDate, String reason) {
			this.commercialSupport = new SupportPeriod(startDate, endDate, reason);
			return this;
		}

		public SupportTimeline build() {
			Assert.notNull(this.openSourceSupport, "openSourceSupport should not be null");
			Assert.notNull(this.commercialSupport, "commercialSupport should not be null");
			return new SupportTimeline(this.initialRelease, this.openSourceSupport, this.commercialSupport);
		}

	}

}
