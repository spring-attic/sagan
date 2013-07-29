package org.springframework.site.domain.documentation;

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
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class DocumentationYamlParserTests {

	private Map<String,List<Project>> projects;

	@Before
	public void setUp() throws Exception {
		InputStream yaml = new ClassPathResource("/test-documentation.yml", getClass()).getInputStream();
		DocumentationYamlParser parser = new DocumentationYamlParser();
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
		assertThat(active.size(), equalTo(1));
		Project project = active.get(0);
		assertThat(project.getId(), equalTo("spring-framework"));
		assertThat(project.getName(), equalTo("Spring Framework"));
		assertThat(project.getReferenceUrl(), equalTo("http://static.springsource.org/spring/docs/{version}/spring-framework-reference/html/"));
		assertThat(project.getApiUrl(), equalTo("http://static.springsource.org/spring/docs/{version}/javadoc-api/"));
		assertThat(project.getGithubUrl(), equalTo("https://github.com/SpringSource/spring-framework"));
	}

	@Test
	public void projectsHaveSupportedVersionsWithCurrentVersionSet() throws IOException {
		List<Project> active = projects.get("active");
		assertThat(active.size(), equalTo(1));
		Project project = active.get(0);

		assertThat(project.getSupportedVersions(), contains(
				new SupportedVersion("3.1.4.RELEASE", false),
				new SupportedVersion("3.2.3.RELEASE", true),
				new SupportedVersion("4.0.0.M1", false)
		));
	}

	@Test
	public void projectHasNoSupportedVersions() throws IOException {
		List<Project> active = projects.get("other");
		assertThat(active.size(), equalTo(1));
		Project project = active.get(0);
		assertThat(project.getSupportedVersions().size(), equalTo(0));
	}

}
