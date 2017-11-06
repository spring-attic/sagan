package sagan.projects.support;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import saganx.AbstractIntegrationTests;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectMetadataServiceTests extends AbstractIntegrationTests {

    @Autowired
    private ProjectMetadataService service;

    @Test
    public void categoriesHaveListOfProjects() throws IOException {
        List<Project> active = service.getProjectsForCategory("active");
        assertThat(active.size(), equalTo(67));
    }

    @Test
    public void projectWithCustomSiteAndRepo() throws IOException {
        Project project = service.getProject("spring-framework");
        assertThat(project.getId(), equalTo("spring-framework"));
        assertThat(project.getName(), equalTo("Spring Framework"));
        assertThat(project.getRepoUrl(), equalTo("http://github.com/spring-projects/spring-framework"));
        assertThat(project.getSiteUrl(), equalTo("http://projects.spring.io/spring-framework"));
        assertThat(project.getCategory(), equalTo("active"));
        assertThat(project.hasSite(), equalTo(true));
    }

    @Test
    public void projectWithNoSite() throws IOException {
        Project project = service.getProject("spring-integration-dsl-groovy");
        assertThat(project.getId(), equalTo("spring-integration-dsl-groovy"));
        assertThat(project.getName(), equalTo("Spring Integration Groovy DSL"));
        assertThat(project.getRepoUrl(), equalTo("http://github.com/spring-projects/spring-integration-dsl-groovy"));
        assertThat(project.getSiteUrl(), equalTo(""));
        assertThat(project.hasSite(), equalTo(false));
    }

    @Test
    public void getSupportedReferenceDocumentVersionsOrdering() {
        List<ProjectRelease> docVersions = service.getProject("spring-framework").getProjectReleases();
        assertThat(
                docVersions.get(0).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/5.0.0.RC4/spring-framework-reference/"));
        assertThat(docVersions.get(1).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/"));
        assertThat(
                docVersions.get(2).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/4.3.12.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/"));
        assertThat(docVersions.get(3).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/"));
        assertThat(docVersions.get(4).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/4.2.9.RELEASE/spring-framework-reference/htmlsingle/"));
    }

    @Test
    public void getSupportedApiDocsUrlsOrdering() {
        List<ProjectRelease> docVersions = service.getProject("spring-framework").getProjectReleases();
        assertThat(docVersions.get(0).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/5.0.0.RC4/javadoc-api/"));
        assertThat(docVersions.get(1).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/javadoc-api/"));
        assertThat(docVersions.get(2).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/4.3.12.BUILD-SNAPSHOT/javadoc-api/"));
        assertThat(docVersions.get(3).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/current/javadoc-api/"));
        assertThat(docVersions.get(4).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/4.2.9.RELEASE/javadoc-api/"));
    }

    @Test
    public void projectWithNoGaReleaseHasCorrectOrdering() throws Exception {
        Project project = service.getProject("spring-boot");
        assertThat(project.getProjectReleases().get(0).isCurrent(), equalTo(false));
        assertThat(project.getProjectReleases().get(1).isCurrent(), equalTo(false));
    }

    @Autowired
    private ProjectMetadataRepository repo;

    // Verify we don't have issue with deleting a release that might occur when using
    // @OrderColumn
    // See https://hibernate.atlassian.net/browse/HHH-1268
    @Test
    public void deleteRelease() {
        Project project = service.getProject("spring-framework");
        ProjectRelease release = project.getProjectReleases().remove(0);
        assertThat(release.getVersion(), equalTo("5.0.0.RC4"));
        service.save(project);

        repo.flush();
    }
}
