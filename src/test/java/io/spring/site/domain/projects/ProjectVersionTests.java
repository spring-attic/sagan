package io.spring.site.domain.projects;

import org.junit.Test;

import static io.spring.site.domain.projects.ProjectRelease.ReleaseStatus.GENERAL_AVAILABILITY;
import static io.spring.site.domain.projects.ProjectRelease.ReleaseStatus.PRERELEASE;
import static io.spring.site.domain.projects.ProjectRelease.ReleaseStatus.SNAPSHOT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectVersionTests {

	@Test
	public void getDisplayNameForGeneralAvailability() {
		ProjectRelease version = new ProjectReleaseBuilder()
				.versionName("1.2.3.RELEASE")
				.releaseStatus(GENERAL_AVAILABILITY).build();

		assertThat(version.getVersionDisplayName(), equalTo("1.2.3"));
	}

	@Test
	public void getDisplayNameForCurrentRelease() {
		ProjectRelease version = new ProjectReleaseBuilder()
				.versionName("1.2.3.RELEASE")
				.releaseStatus(GENERAL_AVAILABILITY)
				.current(true).build();

		assertThat(version.getVersionDisplayName(), equalTo("1.2.3"));
	}

	@Test
	public void getDisplayNameForSnapshotRelease() {
		ProjectRelease version = new ProjectReleaseBuilder()
				.versionName("1.0.0.SNAPSHOT")
				.releaseStatus(SNAPSHOT).build();

		assertThat(version.getVersionDisplayName(), equalTo("1.0.0.SNAPSHOT"));
	}

	@Test
	public void getDisplayNameForMilestoneReleaseVersion() {
		ProjectRelease version = new ProjectReleaseBuilder()
				.versionName("1.2.3.M1")
				.releaseStatus(PRERELEASE).build();

		assertThat(version.getVersionDisplayName(), equalTo("1.2.3.M1"));
	}

	@Test
	public void getDisplayNameForReleaseCandidateVersion() {
		ProjectRelease version = new ProjectReleaseBuilder()
				.versionName("1.2.3.RC1")
				.releaseStatus(PRERELEASE).build();

		assertThat(version.getVersionDisplayName(), equalTo("1.2.3.RC1"));
	}
}
