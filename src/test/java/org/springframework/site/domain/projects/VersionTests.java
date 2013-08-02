package org.springframework.site.domain.projects;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class VersionTests {

	@Test
	public void getVersionNameForCurrentVersion() {
		Version version = new Version("1.2.3.RELEASE", Version.Release.CURRENT);
		assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE (Current)"));
	}

	@Test
	public void getVersionNameForPreReleaseVersion() {
		Version version = new Version("1.2.3.M1", Version.Release.PRERELEASE);
		assertThat(version.getVersionName(), equalTo("1.2.3.M1 (Pre-Release)"));
	}

	@Test
	public void getVersionNameForOtherVersion() {
		Version version = new Version("1.2.3.RELEASE", Version.Release.SUPPORTED);
		assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE"));
	}

	@Test
	public void getShortNameForCurrentVersion() {
		Version version = new Version("1.2.3.RELEASE", Version.Release.CURRENT);
		assertThat(version.getShortName(), equalTo("1.2.3"));
	}

	@Test
	public void getShortNameForPreReleaseVersion() {
		Version version = new Version("1.2.3.M1", Version.Release.PRERELEASE);
		assertThat(version.getShortName(), equalTo("1.2.3.M1"));
	}

	@Test
	public void getShortNameForOtherVersion() {
		Version version = new Version("1.2.3.RC1", Version.Release.PRERELEASE);
		assertThat(version.getShortName(), equalTo("1.2.3.RC1"));
	}
}
