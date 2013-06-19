package org.springframework.site.documentation;

import org.junit.Test;
import org.springframework.site.documentation.ProjectDocumentVersion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectDocumentVersionTests {

    @Test
    public void getVersionNameForCurrentVersion() {
        ProjectDocumentVersion version = new ProjectDocumentVersion("http://project.org/api", "1.2.3.Release", true);
        assertThat(version.getVersionName(), equalTo("1.2.3.Release (Current)"));
    }

    @Test
    public void getVersionNameForOtherVersion() {
        ProjectDocumentVersion version = new ProjectDocumentVersion("http://project.org/api", "1.2.3.Release", false);
        assertThat(version.getVersionName(), equalTo("1.2.3.Release"));
    }
}
