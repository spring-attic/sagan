package org.springframework.site.domain.projects;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectDocumentationTests {

    @Test
    public void getVersionNameForCurrentVersion() {
        ProjectDocumentation version = new ProjectDocumentation("http://project.org/api",
				new Version("1.2.3.RELEASE", Version.Release.CURRENT));
        assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE (Current)"));
    }

    @Test
    public void getVersionNameForPreReleaseVersion() {
        ProjectDocumentation version = new ProjectDocumentation("http://project.org/api",
				new Version("1.2.3.M1", Version.Release.PRERELEASE));
        assertThat(version.getVersionName(), equalTo("1.2.3.M1 (Pre-Release)"));
    }

    @Test
    public void getVersionNameForOtherVersion() {
        ProjectDocumentation version = new ProjectDocumentation("http://project.org/api",
				new Version("1.2.3.RELEASE", Version.Release.SUPPORTED));
        assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE"));
    }
}
