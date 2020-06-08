package sagan.site.projects;

import java.util.regex.Pattern;

import org.springframework.util.Assert;

/**
 * Status of a {@link Release}
 */
public enum ReleaseStatus {
	/**
	 * Unstable version with limited support
	 */
	SNAPSHOT,
	/**
	 * Pre-Release version meant to be tested by the community
	 */
	PRERELEASE,
	/**
	 * Release Generally Available on public artifact repositories and enjoying full support from maintainers
	 */
	GENERAL_AVAILABILITY;

	private static final Pattern PRERELEASE_PATTERN = Pattern.compile("[A-Za-z0-9\\.\\-]+?(M|RC)\\d+");

	private static final String SNAPSHOT_SUFFIX = "SNAPSHOT";

	/**
	 * Deduce the {@link ReleaseStatus status} of a release given its {@link Version}
	 * @param version a project version
	 * @return the release status for this version
	 */
	public static ReleaseStatus getFromVersion(Version version) {
		Assert.notNull(version, "Version must not be null");
		if (version.toString().endsWith(SNAPSHOT_SUFFIX)) {
			return SNAPSHOT;
		}
		if (PRERELEASE_PATTERN.matcher(version.toString()).matches()) {
			return PRERELEASE;
		}
		return GENERAL_AVAILABILITY;
	}
}
