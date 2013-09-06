package io.spring.site.domain.projects;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectMetadataYamlParserTests {

    private ProjectMetadataService service;

    @Before
    public void setUp() throws Exception {
        InputStream yamlInputStream = new ClassPathResource("/test-project-metadata.yml", getClass()).getInputStream();
        ProjectMetadataYamlParser parser = new ProjectMetadataYamlParser();
        service = parser.createServiceFromYaml(yamlInputStream);
    }

    @Test
    public void parsesGhPagesBaseUrl() throws IOException {
        assertThat(service.getGhPagesBaseUrl(), equalTo("http://projects.spring.io"));
    }

    @Test
    public void categoriesHaveListOfProjects() throws IOException {
        List<Project> active = service.getProjectsForCategory("active");
        assertThat(active.size(), equalTo(17));
    }

    @Test
    public void projectWithCustomSiteAndRepo() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(0);
        assertThat(project.getId(), equalTo("spring-framework"));
        assertThat(project.getName(), equalTo("Spring Framework"));
        assertThat(project.getRepoUrl(), equalTo("http://www.example.com/repo/spring-framework"));
        assertThat(project.getSiteUrl(), equalTo("http://www.example.com/spring-framework"));
        assertThat(project.hasSite(), equalTo(true));
    }

    @Test
    public void projectWithDefaultSiteAndRepo() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(2);
        assertThat(project.getId(), equalTo("spring-framework-defaultsite"));
        assertThat(project.getName(), equalTo("Spring Framework"));
        assertThat(project.getRepoUrl(), equalTo("http://github.com/springframework/spring-framework-defaultsite"));
        assertThat(project.getSiteUrl(), equalTo("http://projects.spring.io/spring-framework-defaultsite"));
        assertThat(project.hasSite(), equalTo(true));
    }

    @Test
    public void projectWithNoSite() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(1);
        assertThat(project.getId(), equalTo("spring-framework-nosite"));
        assertThat(project.getName(), equalTo("Spring Framework"));
        assertThat(project.getRepoUrl(), equalTo("http://github.com/springframework/spring-framework-nosite"));
        assertThat(project.getSiteUrl(), equalTo(""));
        assertThat(project.hasSite(), equalTo(false));
    }

    @Test
    public void projectsHasDocumentationWithCurrentVersionSet() throws IOException {
        List<ProjectRelease> versionList = service.getProjectsForCategory("active").get(1).getProjectReleases();
        assertThat(versionList.get(0).getVersion(), equalTo("4.1.1.BUILD-SNAPSHOT"));
        assertThat(versionList.get(0).isSnapshot(), equalTo(true));
        assertThat(versionList.get(0).isCurrent(), equalTo(false));

        assertThat(versionList.get(1).getVersion(), equalTo("4.1.0.SNAPSHOT"));
        assertThat(versionList.get(1).isSnapshot(), equalTo(true));
        assertThat(versionList.get(1).isCurrent(), equalTo(false));

        assertThat(versionList.get(2).getVersion(), equalTo("4.0.0.M1"));
        assertThat(versionList.get(2).isPreRelease(), equalTo(true));
        assertThat(versionList.get(2).isCurrent(), equalTo(false));

        assertThat(versionList.get(3).getVersion(), equalTo("3.2.3.RELEASE"));
        assertThat(versionList.get(3).isGeneralAvailability(), equalTo(true));
        assertThat(versionList.get(3).isCurrent(), equalTo(true));

        assertThat(versionList.get(4).getVersion(), equalTo("3.1.4.RELEASE"));
        assertThat(versionList.get(4).isGeneralAvailability(), equalTo(true));
        assertThat(versionList.get(4).isCurrent(), equalTo(false));
    }

    @Test
    public void projectHasNoSupportedVersions() throws IOException {
        List<Project> active = service.getProjectsForCategory("other");
        assertThat(active.size(), equalTo(1));
        Project project = active.get(0);
        assertThat(project.getProjectReleases().size(), equalTo(0));
    }

    @Test
    public void getSupportedReferenceDocumentVersions() {
        List<ProjectRelease> docVersions = service.getProjectsForCategory("active").get(0).getProjectReleases();
        assertThat(docVersions.get(0).getRefDocUrl(), equalTo("http://docs.spring.io/spring/docs/4.0.0.M1/spring-framework-reference/html/"));
        assertThat(docVersions.get(1).getRefDocUrl(), equalTo("http://docs.spring.io/spring/docs/3.2.3.RELEASE/spring-framework-reference/html/"));
        assertThat(docVersions.get(2).getRefDocUrl(), equalTo("http://docs.spring.io/spring/docs/3.1.4.SNAPSHOT/spring-framework-reference/html/"));
    }

    @Test
    public void getSupportedApiDocsUrls() {
        List<ProjectRelease> docVersions = service.getProjectsForCategory("active").get(0).getProjectReleases();
        assertThat(docVersions.get(0).getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/4.0.0.M1/javadoc-api/"));
        assertThat(docVersions.get(1).getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/3.2.3.RELEASE/javadoc-api/"));
        assertThat(docVersions.get(2).getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/3.1.4.SNAPSHOT/javadoc-api/"));
    }

    @Test
    public void apiAndReferenceDocsUrlsCanBeOverridden() throws Exception {
        Project project = service.getProjectsForCategory("active").get(3);
        ProjectRelease version = project.getProjectReleases().get(0);
        assertThat(version.getVersion(), equalTo("2.2.1.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/spring/trunk/apidocs/index.html"));
        assertThat(version.getRefDocUrl(), equalTo("http://docs.spring.io/spring/trunk/reference/html/index.html"));
    }

    @Test
    public void apiAndReferenceDocsUrlsCanBeOverriddenInAMixedWay() throws Exception {
        Project project = service.getProjectsForCategory("active").get(4);
        ProjectRelease version = project.getProjectReleases().get(0);
        assertThat(version.getVersion(), equalTo("2.2.1.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/spring/trunk/apidocs/index.html"));
        assertThat(version.getRefDocUrl(), equalTo("http://docs.spring.io/spring/trunk/reference/html/index.html"));

        version = project.getProjectReleases().get(1);
        assertThat(version.getVersion(), equalTo("2.1.2.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.1.2.RELEASE/javadoc-api/"));
        assertThat(version.getRefDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.1.2.RELEASE/spring-framework-reference/html/"));
    }

    @Test
    public void justApiDocsUrlCanBeOverridden() throws Exception {
        Project project = service.getProjectsForCategory("active").get(5);
        ProjectRelease version = project.getProjectReleases().get(0);
        assertThat(version.getVersion(), equalTo("2.4.0.M1"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.4.0.M1/api/"));
        assertThat(version.getRefDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.4.0.M1/reference/html"));

        version = project.getProjectReleases().get(1);
        assertThat(version.getVersion(), equalTo("2.3.2.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.3.2.RELEASE/javadoc-api/"));
        assertThat(version.getRefDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.3.2.RELEASE/reference/html"));
    }

    @Test
    public void justRefDocsUrlCanBeOverridden() throws Exception {
        Project project = service.getProjectsForCategory("active").get(6);
        ProjectRelease version = project.getProjectReleases().get(0);
        assertThat(version.getVersion(), equalTo("2.4.0.M1"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.4.0.M1/api/html"));
        assertThat(version.getRefDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.4.0.M1/reference/"));

        version = project.getProjectReleases().get(1);
        assertThat(version.getVersion(), equalTo("2.3.2.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.3.2.RELEASE/api/html"));
        assertThat(version.getRefDocUrl(), equalTo("http://docs.spring.io/spring/docs/2.3.2.RELEASE/other-reference/"));
    }

    @Test
    public void docsUrlCanBeFullHttp() throws Exception {
        Project project = service.getProjectsForCategory("active").get(7);
        ProjectRelease version = project.getProjectReleases().get(0);
        assertThat(version.getVersion(), equalTo("2.4.0.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/api/html"));
        assertThat(version.getRefDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/reference/"));
    }

    @Test
    public void omittedDocsUrlsAreBuiltFromDefaults() throws Exception {
        Project project = service.getProjectsForCategory("active").get(8);
        ProjectRelease version = project.getProjectReleases().get(0);
        assertThat(version.getVersion(), equalTo("2.4.0.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/project-docs-urls-omitted/docs/2.4.0.RELEASE/api/"));
        assertThat(version.getRefDocUrl(), equalTo("http://docs.spring.io/project-docs-urls-omitted/docs/2.4.0.RELEASE/reference/html"));
    }

    @Test
    public void docsUrlsCanBeExplicitlyExcluded() throws Exception {
        Project project = service.getProjectsForCategory("active").get(9);
        ProjectRelease version = project.getProjectReleases().get(0);
        assertThat(version.getVersion(), equalTo("2.4.0.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo(""));
        assertThat(version.getRefDocUrl(), equalTo(""));

        version = project.getProjectReleases().get(1);
        assertThat(version.getVersion(), equalTo("2.3.0.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://www.example.com/spring/docs/2.3.0.RELEASE/api/"));
        assertThat(version.getRefDocUrl(), equalTo("http://www.example.com/spring/docs/2.3.0.RELEASE/reference/html"));
    }

    @Test
    public void docsUrlsCanBeExplicitlyExcludedInOverride() throws Exception {
        Project project = service.getProjectsForCategory("active").get(10);
        ProjectRelease version = project.getProjectReleases().get(0);
        assertThat(version.getVersion(), equalTo("2.4.0.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/api/"));
        assertThat(version.getRefDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/reference/html"));

        version = project.getProjectReleases().get(1);
        assertThat(version.getVersion(), equalTo("2.3.0.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo(""));
        assertThat(version.getRefDocUrl(), equalTo(""));
    }

    @Test
    public void urlsInProjectsCanHaveVariables() throws Exception {
        Project project = service.getProjectsForCategory("active").get(11);
        assertThat(project.getSiteUrl(), equalTo("http://projects.spring.io/foo/project-with-variables-in-urls/"));
        assertThat(project.getRepoUrl(), equalTo("http://github.com/springframework/foo/project-with-variables-in-urls/"));

        ProjectRelease version = project.getProjectReleases().get(0);
        assertThat(version.getRefDocUrl(), equalTo("http://projects.spring.io/docs/overridden/project-with-variables-in-urls/3.1.4.RELEASE"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/overridden/project-with-variables-in-urls/3.1.4.RELEASE/javadoc-api/"));

        version = project.getProjectReleases().get(1);
        assertThat(version.getRefDocUrl(), equalTo("http://projects.spring.io/docs/project-with-variables-in-urls/3.1.1.RELEASE/html/"));
        assertThat(version.getApiDocUrl(), equalTo("http://docs.spring.io/spring/docs/project-with-variables-in-urls/3.1.1.RELEASE/javadoc-api/"));
    }

    @Test
    public void projectWithDefaultGroupId() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(0);
        assertThat(project.getProjectReleases().get(0).getGroupId(), equalTo("org.springframework"));
    }

    @Test
    public void projectWithGroupIdOverriddenInProject() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(12);
        assertThat(project.getProjectReleases().get(0).getGroupId(), equalTo("org.something"));
    }

    @Test
    public void projectWithGroupIdOverriddenInSupportedVersion() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(13);
        assertThat(project.getProjectReleases().get(0).getGroupId(), equalTo("org.something.else"));
        assertThat(project.getProjectReleases().get(1).getGroupId(), equalTo("org.something"));
    }

    @Test
    public void projectWithDefaultArtifactId() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(0);
        assertThat(project.getProjectReleases().get(0).getArtifactId(), equalTo("spring-framework"));
    }

    @Test
    public void projectWithDefaultArtifactIdOverriddenInProject() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(14);
        assertThat(project.getProjectReleases().get(0).getArtifactId(), equalTo("overridden-artifact-id"));
    }

    @Test
    public void nonAggregatorProject() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(14);
        assertThat(project.isAggregator(), equalTo(false));
    }

    @Test
    public void aggregatorProject() throws IOException {
        Project project =  service.getProjectsForCategory("active").get(15);
        assertThat(project.isAggregator(), equalTo(true));
        assertThat(project.getId(), equalTo("aggregator-project"));
    }

    @Test
    public void projectWithNoGaReleaseHasCorrectOrdering() throws Exception {
        Project project =  service.getProjectsForCategory("active").get(16);
        assertThat(project.getProjectReleases().get(0).isCurrent(), equalTo(false));
        assertThat(project.getProjectReleases().get(1).isCurrent(), equalTo(false));
        assertThat(project.getProjectReleases().get(2).isCurrent(), equalTo(false));
        assertThat(project.getProjectReleases().get(0).getVersion(), equalTo("4.0.0.BUILD-SNAPSHOT"));
    }
}
