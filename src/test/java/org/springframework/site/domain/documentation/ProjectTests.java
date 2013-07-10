package org.springframework.site.domain.documentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ProjectTests {

    private Project project;

    @Before
    public void setup() {
        project = new Project(null,
                "Name",
                "https://github.com/someone/project",
                "http://project.org/reference/{version}/index.html", "http://project.org/api/{version}/index.html", Arrays.asList("4.0.0", "3.1.5", "1.2.3"));
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
