package org.springframework.site.domain.projects;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class ProjectMetadataYamlParserTests {

	private Map<String,List<Project>> projects;

	@Before
	public void setUp() throws Exception {
		InputStream yaml = new ClassPathResource("/test-project-metadata.yml", getClass()).getInputStream();
		ProjectMetadataYamlParser parser = new ProjectMetadataYamlParser();
		projects = parser.parse(yaml);
	}

	@Test
	public void parseCategoriesIgnoresDiscardCategory() throws IOException {
		assertThat(projects.size(), equalTo(3));
		assertThat(projects.keySet(), containsInAnyOrder("active", "other", "attic"));
	}

	@Test
	public void categoriesHaveListOfProjects() throws IOException {
		List<Project> active = projects.get("active");
		assertThat(active.size(), equalTo(12));
	}

	@Test
	public void projectWithCustomSiteAndRepo() throws IOException {
		Project project =  projects.get("active").get(0);
		assertThat(project.getId(), equalTo("spring-framework"));
		assertThat(project.getName(), equalTo("Spring Framework"));
		assertThat(project.getRepoUrl(), equalTo("http://www.example.com/repo/spring-framework"));
		assertThat(project.getSiteUrl(), equalTo("http://www.example.com/spring-framework"));
		assertThat(project.hasSite(), equalTo(true));
	}

	@Test
	public void projectWithDefaultSiteAndRepo() throws IOException {
		Project project =  projects.get("active").get(2);
		assertThat(project.getId(), equalTo("spring-framework-defaultsite"));
		assertThat(project.getName(), equalTo("Spring Framework"));
		assertThat(project.getRepoUrl(), equalTo("http://github.com/springframework/spring-framework-defaultsite"));
		assertThat(project.getSiteUrl(), equalTo("http://projects.springframework.io/spring-framework-defaultsite"));
		assertThat(project.hasSite(), equalTo(true));
	}

	@Test
	public void projectWithNoSite() throws IOException {
		Project project =  projects.get("active").get(1);
		assertThat(project.getId(), equalTo("spring-framework-nosite"));
		assertThat(project.getName(), equalTo("Spring Framework"));
		assertThat(project.getRepoUrl(), equalTo("http://github.com/springframework/spring-framework-nosite"));
		assertThat(project.getSiteUrl(), equalTo(""));
		assertThat(project.hasSite(), equalTo(false));
	}

	@Test
	public void projectsHasDocumentationWithCurrentVersionSet() throws IOException {
		List<ProjectVersion> versionList = projects.get("active").get(0).getProjectVersions();
		assertThat(versionList.get(0).getVersion(), equalTo(new Version("4.0.0.M1", Version.Release.PRERELEASE)));
		assertThat(versionList.get(1).getVersion(), equalTo(new Version("3.2.3.RELEASE", Version.Release.CURRENT)));
		assertThat(versionList.get(2).getVersion(), equalTo(new Version("3.1.4.RELEASE", Version.Release.SUPPORTED)));
	}

	@Test
	public void projectHasNoSupportedVersions() throws IOException {
		List<Project> active = projects.get("other");
		assertThat(active.size(), equalTo(1));
		Project project = active.get(0);
		assertThat(project.getProjectVersions().size(), equalTo(0));
	}

	@Test
	public void getSupportedReferenceDocumentVersions() {
		List<ProjectVersion> docVersions = projects.get("active").get(0).getProjectVersions();
		assertThat(docVersions.get(0).getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/4.0.0.M1/spring-framework-reference/html/"));
		assertThat(docVersions.get(1).getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/3.2.3.RELEASE/spring-framework-reference/html/"));
		assertThat(docVersions.get(2).getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/3.1.4.RELEASE/spring-framework-reference/html/"));
	}

	@Test
	public void getSupportedApiDocsUrls() {
		List<ProjectVersion> docVersions = projects.get("active").get(0).getProjectVersions();
		assertThat(docVersions.get(0).getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/4.0.0.M1/javadoc-api/"));
		assertThat(docVersions.get(1).getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/3.2.3.RELEASE/javadoc-api/"));
		assertThat(docVersions.get(2).getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/3.1.4.RELEASE/javadoc-api/"));
	}

	@Test
	public void apiAndReferenceDocsUrlsCanBeOverridden() throws Exception {
		Project project = projects.get("active").get(3);
		ProjectVersion version = project.getProjectVersions().get(0);
		assertThat(version.getVersion(), equalTo(new Version("2.2.1.RELEASE", Version.Release.CURRENT)));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/spring/trunk/apidocs/index.html"));
		assertThat(version.getRefDocUrl(), equalTo("http://static.springsource.org/spring/trunk/reference/html/index.html"));
	}

	@Test
	public void apiAndReferenceDocsUrlsCanBeOverriddenInAMixedWay() throws Exception {
		Project project = projects.get("active").get(4);
		ProjectVersion version = project.getProjectVersions().get(0);
		assertThat(version.getVersion(), equalTo(new Version("2.2.1.RELEASE", Version.Release.CURRENT)));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/spring/trunk/apidocs/index.html"));
		assertThat(version.getRefDocUrl(), equalTo("http://static.springsource.org/spring/trunk/reference/html/index.html"));

		version = project.getProjectVersions().get(1);
		assertThat(version.getVersion(), equalTo(new Version("2.1.2.RELEASE", Version.Release.SUPPORTED)));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.1.2.RELEASE/javadoc-api/"));
		assertThat(version.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.1.2.RELEASE/spring-framework-reference/html/"));
	}

	@Test
	public void justApiDocsUrlCanBeOverridden() throws Exception {
		Project project = projects.get("active").get(5);
		ProjectVersion version = project.getProjectVersions().get(0);
		assertThat(version.getVersion(), equalTo(new Version("2.4.0.M1", Version.Release.PRERELEASE)));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.4.0.M1/api/"));
		assertThat(version.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.4.0.M1/reference/html"));

		version = project.getProjectVersions().get(1);
		assertThat(version.getVersion(), equalTo(new Version("2.3.2.RELEASE", Version.Release.CURRENT)));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.3.2.RELEASE/javadoc-api/"));
		assertThat(version.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.3.2.RELEASE/reference/html"));
	}

	@Test
	public void justRefDocsUrlCanBeOverridden() throws Exception {
		Project project = projects.get("active").get(6);
		ProjectVersion version = project.getProjectVersions().get(0);
		assertThat(version.getVersion(), equalTo(new Version("2.4.0.M1", Version.Release.PRERELEASE)));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.4.0.M1/api/html"));
		assertThat(version.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.4.0.M1/reference/"));

		version = project.getProjectVersions().get(1);
		assertThat(version.getVersion(), equalTo(new Version("2.3.2.RELEASE", Version.Release.CURRENT)));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.3.2.RELEASE/api/html"));
		assertThat(version.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.3.2.RELEASE/other-reference/"));
	}

	@Test
	public void docsUrlCanBeFullHttp() throws Exception {
		Project project = projects.get("active").get(7);
		ProjectVersion version = project.getProjectVersions().get(0);
		assertThat(version.getVersion(), equalTo(new Version("2.4.0.RELEASE", Version.Release.CURRENT)));
		assertThat(version.getApiDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/api/html"));
		assertThat(version.getRefDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/reference/"));
	}

	@Test
	public void omittedDocsUrlsAreBuiltFromDefaults() throws Exception {
		Project project = projects.get("active").get(8);
		ProjectVersion version = project.getProjectVersions().get(0);
		assertThat(version.getVersion(), equalTo(new Version("2.4.0.RELEASE", Version.Release.CURRENT)));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/project-docs-urls-omitted/docs/2.4.0.RELEASE/api/"));
		assertThat(version.getRefDocUrl(), equalTo("http://static.springsource.org/project-docs-urls-omitted/docs/2.4.0.RELEASE/reference/html"));
	}

	@Test
	public void docsUrlsCanBeExplicitlyExcluded() throws Exception {
		Project project = projects.get("active").get(9);
		ProjectVersion version = project.getProjectVersions().get(0);
		assertThat(version.getVersion(), equalTo(new Version("2.4.0.RELEASE", Version.Release.CURRENT)));
		assertThat(version.getApiDocUrl(), equalTo(""));
		assertThat(version.getRefDocUrl(), equalTo(""));

		version = project.getProjectVersions().get(1);
		assertThat(version.getVersion(), equalTo(new Version("2.3.0.RELEASE", Version.Release.SUPPORTED)));
		assertThat(version.getApiDocUrl(), equalTo("http://www.example.com/spring/docs/2.3.0.RELEASE/api/"));
		assertThat(version.getRefDocUrl(), equalTo("http://www.example.com/spring/docs/2.3.0.RELEASE/reference/html"));
	}

	@Test
	public void docsUrlsCanBeExplicitlyExcludedInOverride() throws Exception {
		Project project = projects.get("active").get(10);
		ProjectVersion version = project.getProjectVersions().get(0);
		assertThat(version.getVersion(), equalTo(new Version("2.4.0.RELEASE", Version.Release.CURRENT)));
		assertThat(version.getApiDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/api/"));
		assertThat(version.getRefDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/reference/html"));

		version = project.getProjectVersions().get(1);
		assertThat(version.getVersion(), equalTo(new Version("2.3.0.RELEASE", Version.Release.SUPPORTED)));
		assertThat(version.getApiDocUrl(), equalTo(""));
		assertThat(version.getRefDocUrl(), equalTo(""));
	}

	@Test
	public void urlsInProjectsCanHaveVariables() throws Exception {
		Project project = projects.get("active").get(11);
		assertThat(project.getSiteUrl(), equalTo("http://projects.springframework.io/foo/project-with-variables-in-urls/"));
		assertThat(project.getRepoUrl(), equalTo("http://github.com/springframework/foo/project-with-variables-in-urls/"));

		ProjectVersion version = project.getProjectVersions().get(0);
		assertThat(version.getRefDocUrl(), equalTo("http://projects.springframework.io/docs/overridden/project-with-variables-in-urls/3.1.4.RELEASE"));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/overridden/project-with-variables-in-urls/3.1.4.RELEASE/javadoc-api/"));

		version = project.getProjectVersions().get(1);
		assertThat(version.getRefDocUrl(), equalTo("http://projects.springframework.io/docs/project-with-variables-in-urls/3.1.1.RELEASE/html/"));
		assertThat(version.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/project-with-variables-in-urls/3.1.1.RELEASE/javadoc-api/"));
	}

}
