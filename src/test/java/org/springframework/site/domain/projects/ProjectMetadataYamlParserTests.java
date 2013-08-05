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
import static org.hamcrest.Matchers.nullValue;

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
		assertThat(active.size(), equalTo(9));
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
		assertThat(project.getSiteUrl(), nullValue());
		assertThat(project.hasSite(), equalTo(false));
	}

	@Test
	public void projectsHasDocumentationWithCurrentVersionSet() throws IOException {
		List<ProjectVersion> documentationList = projects.get("active").get(0).getProjectVersions();
		assertThat(documentationList.get(0).getVersion(), equalTo(new Version("4.0.0.M1", Version.Release.PRERELEASE)));
		assertThat(documentationList.get(1).getVersion(), equalTo(new Version("3.2.3.RELEASE", Version.Release.CURRENT)));
		assertThat(documentationList.get(2).getVersion(), equalTo(new Version("3.1.4.RELEASE", Version.Release.SUPPORTED)));
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
		ProjectVersion documentation = project.getProjectVersions().get(0);
		assertThat(documentation.getVersion(), equalTo(new Version("2.2.1.RELEASE", Version.Release.CURRENT)));
		assertThat(documentation.getApiDocUrl(), equalTo("http://static.springsource.org/spring/trunk/apidocs/index.html"));
		assertThat(documentation.getRefDocUrl(), equalTo("http://static.springsource.org/spring/trunk/reference/html/index.html"));
	}

	@Test
	public void apiAndReferenceDocsUrlsCanBeOverriddenInAMixedWay() throws Exception {
		Project project = projects.get("active").get(4);
		ProjectVersion documentation = project.getProjectVersions().get(0);
		assertThat(documentation.getVersion(), equalTo(new Version("2.2.1.RELEASE", Version.Release.CURRENT)));
		assertThat(documentation.getApiDocUrl(), equalTo("http://static.springsource.org/spring/trunk/apidocs/index.html"));
		assertThat(documentation.getRefDocUrl(), equalTo("http://static.springsource.org/spring/trunk/reference/html/index.html"));

		documentation = project.getProjectVersions().get(1);
		assertThat(documentation.getVersion(), equalTo(new Version("2.1.2.RELEASE", Version.Release.SUPPORTED)));
		assertThat(documentation.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.1.2.RELEASE/javadoc-api/"));
		assertThat(documentation.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.1.2.RELEASE/spring-framework-reference/html/"));
	}

	@Test
	public void justApiDocsUrlCanBeOverridden() throws Exception {
		Project project = projects.get("active").get(5);
		ProjectVersion documentation = project.getProjectVersions().get(0);
		assertThat(documentation.getVersion(), equalTo(new Version("2.4.0.M1", Version.Release.PRERELEASE)));
		assertThat(documentation.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.4.0.M1/api/"));
		assertThat(documentation.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.4.0.M1/reference/html"));

		documentation = project.getProjectVersions().get(1);
		assertThat(documentation.getVersion(), equalTo(new Version("2.3.2.RELEASE", Version.Release.CURRENT)));
		assertThat(documentation.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.3.2.RELEASE/javadoc-api/"));
		assertThat(documentation.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.3.2.RELEASE/reference/html"));
	}

	@Test
	public void justRefDocsUrlCanBeOverridden() throws Exception {
		Project project = projects.get("active").get(6);
		ProjectVersion documentation = project.getProjectVersions().get(0);
		assertThat(documentation.getVersion(), equalTo(new Version("2.4.0.M1", Version.Release.PRERELEASE)));
		assertThat(documentation.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.4.0.M1/api/html"));
		assertThat(documentation.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.4.0.M1/reference/"));

		documentation = project.getProjectVersions().get(1);
		assertThat(documentation.getVersion(), equalTo(new Version("2.3.2.RELEASE", Version.Release.CURRENT)));
		assertThat(documentation.getApiDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.3.2.RELEASE/api/html"));
		assertThat(documentation.getRefDocUrl(), equalTo("http://static.springsource.org/spring/docs/2.3.2.RELEASE/other-reference/"));
	}

	@Test
	public void docsUrlCanBeFullHttp() throws Exception {
		Project project = projects.get("active").get(7);
		ProjectVersion documentation = project.getProjectVersions().get(0);
		assertThat(documentation.getVersion(), equalTo(new Version("2.4.0.RELEASE", Version.Release.CURRENT)));
		assertThat(documentation.getApiDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/api/html"));
		assertThat(documentation.getRefDocUrl(), equalTo("http://www.example.com/spring/docs/2.4.0.RELEASE/reference/"));
	}

	@Test
	public void omittedDocsUrlsAreBuiltFromDefaults() throws Exception {
		Project project = projects.get("active").get(8);
		ProjectVersion documentation = project.getProjectVersions().get(0);
		assertThat(documentation.getVersion(), equalTo(new Version("2.4.0.RELEASE", Version.Release.CURRENT)));
		assertThat(documentation.getApiDocUrl(), equalTo("http://static.springsource.org/project-docs-urls-omitted/docs/2.4.0.RELEASE/api"));
		assertThat(documentation.getRefDocUrl(), equalTo("http://static.springsource.org/project-docs-urls-omitted/docs/2.4.0.RELEASE/reference/html"));
	}
}
