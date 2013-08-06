package org.springframework.site.domain.projects;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectVersionTests {

	@Test
	public void getShortNameForCurrentVersion() {
		ProjectVersion version = new ProjectVersion("1.2.3.RELEASE", ProjectVersion.Release.CURRENT, "", "");
		assertThat(version.getShortName(), equalTo("1.2.3"));
	}

	@Test
	public void getShortNameForPreReleaseVersion() {
		ProjectVersion version = new ProjectVersion("1.2.3.M1", ProjectVersion.Release.PRERELEASE, "", "");
		assertThat(version.getShortName(), equalTo("1.2.3.M1"));
	}

	@Test
	public void getShortNameForOtherVersion() {
		ProjectVersion version = new ProjectVersion("1.2.3.RC1", ProjectVersion.Release.PRERELEASE,"", "");
		assertThat(version.getShortName(), equalTo("1.2.3.RC1"));
	}
}
