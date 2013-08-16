package org.springframework.site.domain.projects;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectVersionTests {

	@Test
	public void getDisplayNameForCurrentVersion() {
		ProjectRelease version = new ProjectRelease("1.2.3.RELEASE", ProjectRelease.ReleaseStatus.CURRENT, "", "", "", "");
		assertThat(version.getVersionDisplayName(), equalTo("1.2.3"));
	}

	@Test
	public void getDisplayNameForPreReleaseVersion() {
		ProjectRelease version = new ProjectRelease("1.2.3.M1", ProjectRelease.ReleaseStatus.PRERELEASE, "", "", "", "");
		assertThat(version.getVersionDisplayName(), equalTo("1.2.3.M1"));
	}

	@Test
	public void getDisplayNameForOtherVersion() {
		ProjectRelease version = new ProjectRelease("1.2.3.RC1", ProjectRelease.ReleaseStatus.PRERELEASE,"", "", "", "");
		assertThat(version.getVersionDisplayName(), equalTo("1.2.3.RC1"));
	}
}
