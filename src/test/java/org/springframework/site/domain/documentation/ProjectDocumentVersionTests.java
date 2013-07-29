package org.springframework.site.domain.documentation;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectDocumentVersionTests {

    @Test
    public void getVersionNameForCurrentVersion() {
        ProjectDocumentVersion version = new ProjectDocumentVersion("http://project.org/api",
				new SupportedVersion("1.2.3.RELEASE", true));
        assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE (Current)"));
    }

    @Test
    public void getVersionNameForOtherVersion() {
        ProjectDocumentVersion version = new ProjectDocumentVersion("http://project.org/api",
				new SupportedVersion("1.2.3.RELEASE", false));
        assertThat(version.getVersionName(), equalTo("1.2.3.RELEASE"));
    }
}
