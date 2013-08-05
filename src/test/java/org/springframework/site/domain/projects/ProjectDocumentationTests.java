package org.springframework.site.domain.projects;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectDocumentationTests {

	public static final String API_DOC_URL = "http://project.org/api";
	public static final String REF_DOC_URL = "http://project.org/refdoc";

	@Test
	public void getVersionNameForCurrentVersion() {
		Version version = new Version("1.2.3.RELEASE", Version.Release.CURRENT);
		ProjectDocumentation documentation = new ProjectDocumentation(REF_DOC_URL, API_DOC_URL, version);
		assertThat(documentation.getVersionName(), equalTo("1.2.3.RELEASE (Current)"));
	}

	@Test
	public void getVersionNameForPreReleaseVersion() {
		Version version = new Version("1.2.3.M1", Version.Release.PRERELEASE);
		ProjectDocumentation documentation = new ProjectDocumentation(REF_DOC_URL, API_DOC_URL, version);
		assertThat(documentation.getVersionName(), equalTo("1.2.3.M1 (Pre-Release)"));
	}

	@Test
	public void getVersionNameForOtherVersion() {
		Version version = new Version("1.2.3.RELEASE", Version.Release.SUPPORTED);
		ProjectDocumentation documentation = new ProjectDocumentation(REF_DOC_URL, API_DOC_URL, version);
		assertThat(documentation.getVersionName(), equalTo("1.2.3.RELEASE"));
	}
}
