package io.spring.site.domain.projects;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectVersionTests {

	@Test
	public void getDisplayNameForGeneralAvailability() {
		ProjectRelease version = new ProjectRelease("1.2.3.RELEASE", ProjectRelease.ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", "");
		assertThat(version.getVersionDisplayName(), equalTo("1.2.3"));
	}

	@Test
	public void getDisplayNameForCurrentRelease() {
		ProjectRelease version = new ProjectRelease("1.2.3.RELEASE", ProjectRelease.ReleaseStatus.GENERAL_AVAILABILITY, true, "", "", "", "");
		assertThat(version.getVersionDisplayName(), equalTo("1.2.3"));
	}

	@Test
	public void getDisplayNameForSnapshotRelease() {
		ProjectRelease version = new ProjectRelease("1.0.0.SNAPSHOT", ProjectRelease.ReleaseStatus.SNAPSHOT, false, "", "", "", "");
		assertThat(version.getVersionDisplayName(), equalTo("1.0.0.SNAPSHOT"));
	}

	@Test
	public void getDisplayNameForMilestoneReleaseVersion() {
		ProjectRelease version = new ProjectRelease("1.2.3.M1", ProjectRelease.ReleaseStatus.PRERELEASE, false, "", "", "", "");
		assertThat(version.getVersionDisplayName(), equalTo("1.2.3.M1"));
	}

	@Test
	public void getDisplayNameForReleaseCandidateVersion() {
		ProjectRelease version = new ProjectRelease("1.2.3.RC1", ProjectRelease.ReleaseStatus.PRERELEASE, false, "", "", "", "");
		assertThat(version.getVersionDisplayName(), equalTo("1.2.3.RC1"));
	}
}
