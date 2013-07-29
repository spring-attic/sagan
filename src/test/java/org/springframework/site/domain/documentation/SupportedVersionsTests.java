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
				Arrays.asList("3.1.5.RELEASE", "3.2.3.RELEASE", "4.0.0.M1"));
		assertThat(supportedVersions, contains(
				new SupportedVersion("3.1.5.RELEASE", false),
				new SupportedVersion("3.2.3.RELEASE", true),
				new SupportedVersion("4.0.0.M1", false)
		));
	}

	@Test
	public void setsCurrentVersionWithoutReleases() throws Exception {
		SupportedVersions supportedVersions = SupportedVersions.build(
				Arrays.asList("3.1.x", "3.2.x", "4.0.0.M1"));
		assertThat(supportedVersions, contains(
				new SupportedVersion("3.1.x", false),
				new SupportedVersion("3.2.x", true),
				new SupportedVersion("4.0.0.M1", false)
		));
	}

	@Test
	public void getCurrent() {
		SupportedVersion currentVersion = new SupportedVersion("3.2.3.RELEASE", true);
		SupportedVersions supportedVersions = new SupportedVersions(Arrays.asList(
				new SupportedVersion("3.1.5.RELEASE", false),
				currentVersion
		));
		assertThat(supportedVersions.getCurrent(), equalTo(currentVersion));
	}

}
