package org.springframework.site.domain.projects;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class SupportedVersionsTests {

	@Test
	public void setsCurrentVersionWithReleases() throws Exception {
		List<Version> supportedVersions = SupportedVersions.build(
				Arrays.asList(
						"4.0.0.RC1",
						"4.0.0.M1",
						"3.2.3.RELEASE",
						"3.1.5.RELEASE"
				));

		assertThat(supportedVersions, contains(
				new Version("4.0.0.RC1", Version.Release.PRERELEASE),
				new Version("4.0.0.M1", Version.Release.PRERELEASE),
				new Version("3.2.3.RELEASE", Version.Release.CURRENT),
				new Version("3.1.5.RELEASE", Version.Release.SUPPORTED)
		));
	}

	@Test
	public void sortsVersionInDescendingOrder() throws Exception {
		List<Version> supportedVersions = SupportedVersions.build(
				Arrays.asList(
						"3.2.3.RELEASE",
						"4.0.0.M1",
						"3.1.5.RELEASE",
						"4.0.0.RC1"
				));

		assertThat(supportedVersions, contains(
				new Version("4.0.0.RC1", Version.Release.PRERELEASE),
				new Version("4.0.0.M1", Version.Release.PRERELEASE),
				new Version("3.2.3.RELEASE", Version.Release.CURRENT),
				new Version("3.1.5.RELEASE", Version.Release.SUPPORTED)
		));
	}


}
