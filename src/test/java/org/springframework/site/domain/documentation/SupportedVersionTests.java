package org.springframework.site.domain.documentation;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SupportedVersionTests {

	@Test
	public void getVersionNameForCurrentVersion() {
		SupportedVersion version = new SupportedVersion("1.2.3.RELEASE", SupportedVersion.Status.CURRENT);
		assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE (Current)"));
	}

	@Test
	public void getVersionNameForPreReleaseVersion() {
		SupportedVersion version = new SupportedVersion("1.2.3.M1", SupportedVersion.Status.PRERELEASE);
		assertThat(version.getVersionName(), equalTo("1.2.3.M1 (Pre-Release)"));
	}

	@Test
	public void getVersionNameForOtherVersion() {
		SupportedVersion version = new SupportedVersion("1.2.3.RELEASE", SupportedVersion.Status.SUPPORTED);
		assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE"));
	}
}
