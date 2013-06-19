package org.springframework.site.documentation;

import org.junit.Test;
import org.junit.Before;
import org.springframework.site.documentation.Project;
import org.springframework.site.documentation.ProjectDocumentVersion;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectTests {

    private Project project;

    @Before
    public void setup() {
        List<String> supportedVersions = new ArrayList<String>();

        supportedVersions.add("4.0.0");
        supportedVersions.add("3.1.5");
        supportedVersions.add("1.2.3");

        project = new Project("Name",
                "https://github.com/someone/project",
                "http://project.org/reference/{version}/index.html",
                "http://project.org/api/{version}/index.html", supportedVersions);
    }

    @Test
    public void getSupportedReferenceDocumentVersions() {
        List<ProjectDocumentVersion> docVersions = project.getSupportedReferenceDocumentVersions();
        assertThat(docVersions.get(0).getUrl(), equalTo("http://project.org/reference/4.0.0/index.html"));
        assertThat(docVersions.get(1).getUrl(), equalTo("http://project.org/reference/3.1.5/index.html"));
        assertThat(docVersions.get(2).getUrl(), equalTo("http://project.org/reference/1.2.3/index.html"));
    }

    @Test
    public void getSupportedApiDocsUrls() {
        List<ProjectDocumentVersion> docVersions = project.getSupportedApiDocsDocumentVersions();
        assertThat(docVersions.get(0).getUrl(), equalTo("http://project.org/api/4.0.0/index.html"));
        assertThat(docVersions.get(1).getUrl(), equalTo("http://project.org/api/3.1.5/index.html"));
        assertThat(docVersions.get(2).getUrl(), equalTo("http://project.org/api/1.2.3/index.html"));
    }
}
