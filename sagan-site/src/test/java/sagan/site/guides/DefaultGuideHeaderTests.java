package sagan.site.guides;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link DefaultGuideHeader}.
 */
public class DefaultGuideHeaderTests {

	private DefaultGuideHeader guide;

	@Before
	public void setUp() throws Exception {
		Set<String> projects = new HashSet<>();
		projects.add("spring-boot");
		this.guide = new DefaultGuideHeader("rest-service", "spring-guides/gs-rest-service",
				"Rest Service Title", "Description",
				"https://github.com/spring-guides/gs-rest-service",
				"git://github.com/spring-guides/gs-rest-service.git",
				"git@github.com:spring-guides/gs-rest-service.git",
				"https://github.com/spring-guides/gs-rest-service.git",
				projects);
	}

	@Test
	public void testHeaderData() {
		assertThat(guide.getName()).isEqualTo("rest-service");
		assertThat(guide.getRepositoryName()).isEqualTo("spring-guides/gs-rest-service");
		assertThat(guide.getTitle()).isEqualTo("Rest Service Title");
		assertThat(guide.getDescription()).isEqualTo("Description");
		assertThat(guide.getGithubUrl()).isEqualTo("https://github.com/spring-guides/gs-rest-service");
		assertThat(guide.getGitUrl()).isEqualTo("git://github.com/spring-guides/gs-rest-service.git");
		assertThat(guide.getSshUrl()).isEqualTo("git@github.com:spring-guides/gs-rest-service.git");
		assertThat(guide.getCloneUrl()).isEqualTo("https://github.com/spring-guides/gs-rest-service.git");
		assertThat(guide.getZipUrl()).isEqualTo("https://github.com/spring-guides/gs-rest-service/archive/master.zip");
		assertThat(guide.getCiLatestUrl()).isEqualTo("https://travis-ci.org/spring-guides/gs-rest-service");
		assertThat(guide.getCiStatusImageUrl()).isEqualTo("https://travis-ci.org/spring-guides/gs-rest-service.svg?branch=master");
		assertThat(guide.getProjects()).containsExactly("spring-boot");
	}

	@Test
	public void testEmptyProjectList() throws Exception {
		guide = new DefaultGuideHeader("rest-service", "spring-guides/gs-rest-service",
				"Rest Service Title", "Description",
				"https://github.com/spring-guides/gs-rest-service",
				"git://github.com/spring-guides/gs-rest-service.git",
				"git@github.com:spring-guides/gs-rest-service.git",
				"https://github.com/spring-guides/gs-rest-service.git",
				null);
		assertThat(guide.getProjects()).isNotNull().isEmpty();
	}

}
