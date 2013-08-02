package org.springframework.site.domain.projects;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectTests {

    private Project project;

    @Before
    public void setup() {
		project = new Project(null,
				"Name",
				"http://project.org/reference/{version}/index.html", "http://project.org/api/{version}/index.html",
				SupportedVersions.build(Arrays.asList("4.0.0.RELEASE", "3.1.5.RELEASE", "1.2.3.RELEASE")),
				"http://www.example.com/repo/spring-framework",
				"http://www.example.com/spring-framework");
	}

    @Test
    public void getSupportedReferenceDocumentVersions() {
        List<ProjectDocumentation> docVersions = project.getSupportedReferenceDocumentVersions();
        assertThat(docVersions.get(0).getUrl(), equalTo("http://project.org/reference/4.0.0.RELEASE/index.html"));
        assertThat(docVersions.get(1).getUrl(), equalTo("http://project.org/reference/3.1.5.RELEASE/index.html"));
        assertThat(docVersions.get(2).getUrl(), equalTo("http://project.org/reference/1.2.3.RELEASE/index.html"));
    }

    @Test
    public void getSupportedApiDocsUrls() {
        List<ProjectDocumentation> docVersions = project.getSupportedApiDocsDocumentVersions();
        assertThat(docVersions.get(0).getUrl(), equalTo("http://project.org/api/4.0.0.RELEASE/index.html"));
        assertThat(docVersions.get(1).getUrl(), equalTo("http://project.org/api/3.1.5.RELEASE/index.html"));
        assertThat(docVersions.get(2).getUrl(), equalTo("http://project.org/api/1.2.3.RELEASE/index.html"));
    }
}
