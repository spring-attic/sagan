package sagan.docs.search;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectReleaseBuilder;
import sagan.search.SearchEntry;

import java.io.InputStream;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static sagan.projects.ProjectRelease.ReleaseStatus.GENERAL_AVAILABILITY;

public class ApiDocumentMapperTests {

    private Project project = new Project("spring",
            "Spring Project",
            "http://www.example.com/repo/spring-framework",
            "http://www.example.com/spring-framework",
            Collections.<ProjectRelease> emptyList(),
            false,
            "release");

    private ProjectRelease version = new ProjectReleaseBuilder()
            .versionName("3.2.1.RELEASE")
            .releaseStatus(GENERAL_AVAILABILITY)
            .current(true)
            .build();

    private ApiDocumentMapper apiDocumentMapper = new ApiDocumentMapper(project, version);

    @Test
    public void mapOlderJdkApiDocContent() throws Exception {
        InputStream html = new ClassPathResource("/fixtures/apidocs/apiDocument.html", getClass()).getInputStream();
        Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");

        SearchEntry searchEntry = apiDocumentMapper.map(document);
        assertThat(searchEntry.getRawContent(), equalTo("SomeClass"));
    }

    @Test
    public void mapJdk7ApiDocContent() throws Exception {
        InputStream html = new ClassPathResource("/fixtures/apidocs/jdk7javaDoc.html", getClass()).getInputStream();
        Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");

        SearchEntry searchEntry = apiDocumentMapper.map(document);
        assertThat(searchEntry.getRawContent(), equalTo(document.select(".block").text()));
    }

    @Test
    public void mapApiDoc() throws Exception {
        InputStream html = new ClassPathResource("/fixtures/apidocs/jdk7javaDoc.html", getClass()).getInputStream();
        Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");

        SearchEntry searchEntry = apiDocumentMapper.map(document);
        assertThat(searchEntry.getType(), equalTo("apiDoc"));
        assertThat(searchEntry.getVersion(), equalTo("3.2.1.RELEASE"));
        assertThat(searchEntry.getProjectId(), equalTo(project.getId()));
        assertThat(searchEntry.getSubTitle(), equalTo("Spring Project (3.2.1.RELEASE API)"));
    }
}
