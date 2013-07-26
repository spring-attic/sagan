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

		assertThat(project.getSupportedVersions().size(), equalTo(3));
		assertThat(project.getSupportedVersions().get(0), equalTo("3.1.4.RELEASE"));
		assertThat(project.getSupportedVersions().get(1), equalTo("3.2.3.RELEASE"));
		assertThat(project.getSupportedVersions().get(2), equalTo("4.0.0.M1"));
	}

	@Test
	public void projectHasNoSupportedVersions() throws IOException {
		List<Project> active = projects.get("other");
		assertThat(active.size(), equalTo(1));
		Project project = active.get(0);
		assertThat(project.getSupportedVersions().size(), equalTo(0));
	}

}
