package sagan.site.projects;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReleaseStatus}
 */
public class ReleaseStatusTests {

	@Test
	public void shouldDeduceReleaseStatusFromVersion() {
		assertVersionHasReleaseStatus("1.2.0", ReleaseStatus.GENERAL_AVAILABILITY);
		assertVersionHasReleaseStatus("1.2.0-M1", ReleaseStatus.PRERELEASE);
		assertVersionHasReleaseStatus("1.2.0-SNAPSHOT", ReleaseStatus.SNAPSHOT);

		assertVersionHasReleaseStatus("Something", ReleaseStatus.GENERAL_AVAILABILITY);
		assertVersionHasReleaseStatus("Something-SR1", ReleaseStatus.GENERAL_AVAILABILITY);
		assertVersionHasReleaseStatus("Something-M2", ReleaseStatus.PRERELEASE);
		assertVersionHasReleaseStatus("Something-SNAPSHOT", ReleaseStatus.SNAPSHOT);
	}

	@Test
	public void shouldDeduceReleaseStatusFromPreviousVersionScheme() {
		assertVersionHasReleaseStatus("1.2.0.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY);
		assertVersionHasReleaseStatus("1.2.0.M1", ReleaseStatus.PRERELEASE);
		assertVersionHasReleaseStatus("1.2.0.BUILD-SNAPSHOT", ReleaseStatus.SNAPSHOT);

		assertVersionHasReleaseStatus("Something", ReleaseStatus.GENERAL_AVAILABILITY);
		assertVersionHasReleaseStatus("Something.SR1", ReleaseStatus.GENERAL_AVAILABILITY);
		assertVersionHasReleaseStatus("Something.M2", ReleaseStatus.PRERELEASE);
		assertVersionHasReleaseStatus("Something.SNAPSHOT", ReleaseStatus.SNAPSHOT);
	}

	private void assertVersionHasReleaseStatus(String version, ReleaseStatus releaseStatus) {
		assertThat(ReleaseStatus.getFromVersion(Version.of(version))).isEqualTo(releaseStatus);
	}

}