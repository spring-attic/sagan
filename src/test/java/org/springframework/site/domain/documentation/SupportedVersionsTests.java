package org.springframework.site.domain.documentation;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class SupportedVersionsTests {

	@Test
	public void setsCurrentVersionWithReleases() throws Exception {
		SupportedVersions supportedVersions = SupportedVersions.build(
				Arrays.asList(
						"4.0.0.RC1",
						"4.0.0.M1",
						"3.2.3.RELEASE",
						"3.1.5.RELEASE"
				));
		assertThat(supportedVersions, contains(
				new SupportedVersion("4.0.0.RC1", SupportedVersion.Release.PRERELEASE),
				new SupportedVersion("4.0.0.M1", SupportedVersion.Release.PRERELEASE),
				new SupportedVersion("3.2.3.RELEASE", SupportedVersion.Release.CURRENT),
				new SupportedVersion("3.1.5.RELEASE", SupportedVersion.Release.SUPPORTED)
		));
	}

	@Test
	public void sortsVersionInDescendingOrder() throws Exception {
		SupportedVersions supportedVersions = SupportedVersions.build(
				Arrays.asList(
						"3.2.3.RELEASE",
						"4.0.0.M1",
						"3.1.5.RELEASE",
						"4.0.0.RC1"
				));
		assertThat(supportedVersions, contains(
				new SupportedVersion("4.0.0.RC1", SupportedVersion.Release.PRERELEASE),
				new SupportedVersion("4.0.0.M1", SupportedVersion.Release.PRERELEASE),
				new SupportedVersion("3.2.3.RELEASE", SupportedVersion.Release.CURRENT),
				new SupportedVersion("3.1.5.RELEASE", SupportedVersion.Release.SUPPORTED)
		));
	}


	@Test
	public void getCurrent() {
		SupportedVersion currentVersion = new SupportedVersion("3.2.3.RELEASE", SupportedVersion.Release.CURRENT);
		SupportedVersions supportedVersions = new SupportedVersions(Arrays.asList(
				new SupportedVersion("3.1.5.RELEASE", SupportedVersion.Release.SUPPORTED),
				currentVersion
		));
		assertThat(supportedVersions.getCurrent(), equalTo(currentVersion));
	}

}
