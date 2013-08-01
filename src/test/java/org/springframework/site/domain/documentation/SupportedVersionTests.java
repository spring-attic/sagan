package org.springframework.site.domain.documentation;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SupportedVersionTests {

	@Test
	public void getVersionNameForCurrentVersion() {
		SupportedVersion version = new SupportedVersion("1.2.3.RELEASE", SupportedVersion.Release.CURRENT);
		assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE (Current)"));
	}

	@Test
	public void getVersionNameForPreReleaseVersion() {
		SupportedVersion version = new SupportedVersion("1.2.3.M1", SupportedVersion.Release.PRERELEASE);
		assertThat(version.getVersionName(), equalTo("1.2.3.M1 (Pre-Release)"));
	}

	@Test
	public void getVersionNameForOtherVersion() {
		SupportedVersion version = new SupportedVersion("1.2.3.RELEASE", SupportedVersion.Release.SUPPORTED);
		assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE"));
	}

	@Test
	public void getShortNameForCurrentVersion() {
		SupportedVersion version = new SupportedVersion("1.2.3.RELEASE", SupportedVersion.Release.CURRENT);
		assertThat(version.getShortName(), equalTo("1.2.3"));
	}

	@Test
	public void getShortNameForPreReleaseVersion() {
		SupportedVersion version = new SupportedVersion("1.2.3.M1", SupportedVersion.Release.PRERELEASE);
		assertThat(version.getShortName(), equalTo("1.2.3"));
	}

	@Test
	public void getShortNameForOtherVersion() {
		SupportedVersion version = new SupportedVersion("1.2.3.RC1", SupportedVersion.Release.PRERELEASE);
		assertThat(version.getShortName(), equalTo("1.2.3"));
	}
}
