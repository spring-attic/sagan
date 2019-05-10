package sagan.renderer.guides;

import java.util.Arrays;

import org.junit.Test;
import sagan.renderer.github.Repository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GuideResource}
 */
public class GuideResourceTests {

	@Test
	public void nullRepositoryDescription() {
		Repository repository = new Repository(12L, "gs-sample-guide",
				"spring-guides/gs-sample-guide", null,
				"http://example.org/spring-guides/gs-sample-guide",
				"git://example.org/spring-guides/gs-sample-guide.git",
				"git@example.org:spring-guides/gs-sample-guide.git",
				"https://example.org/spring-guides/gs-sample-guide.git",
				null);
		GuideResource guideResource = new GuideResource(repository);
		assertThat(guideResource.getName()).isEqualTo("sample-guide");
		assertThat(guideResource.getRepositoryName()).isEqualTo("spring-guides/gs-sample-guide");
		assertThat(guideResource.getTitle()).isEmpty();
		assertThat(guideResource.getDescription()).isEmpty();
		assertThat(guideResource.getType()).isEqualTo(GuideType.GETTING_STARTED);
		assertThat(guideResource.getGithubUrl()).isEqualTo("http://example.org/spring-guides/gs-sample-guide");
		assertThat(guideResource.getCloneUrl()).isEqualTo("https://example.org/spring-guides/gs-sample-guide.git");
		assertThat(guideResource.getGitUrl()).isEqualTo("git://example.org/spring-guides/gs-sample-guide.git");
		assertThat(guideResource.getSshUrl()).isEqualTo("git@example.org:spring-guides/gs-sample-guide.git");
		assertThat(guideResource.getProjects()).isEmpty();
	}

	@Test
	public void noGuideProjects() {
		Repository repository = new Repository(12L, "tut-sample-guide",
				"spring-guides/tut-sample-guide", "Title :: Description",
				"http://example.org/spring-guides/tut-sample-guide",
				"git://example.org/spring-guides/tut-sample-guide.git",
				"git@example.org:spring-guides/tut-sample-guide.git",
				"https://example.org/spring-guides/tut-sample-guide.git",
				null);
		GuideResource guideResource = new GuideResource(repository);
		assertThat(guideResource.getName()).isEqualTo("sample-guide");
		assertThat(guideResource.getRepositoryName()).isEqualTo("spring-guides/tut-sample-guide");
		assertThat(guideResource.getTitle()).isEqualTo("Title");
		assertThat(guideResource.getDescription()).isEqualTo("Description");
		assertThat(guideResource.getType()).isEqualTo(GuideType.TUTORIAL);
		assertThat(guideResource.getProjects()).isEmpty();
	}

	@Test
	public void withGuideProjects() {
		Repository repository = new Repository(12L, "top-sample-guide",
				"spring-guides/top-sample-guide",
				"Title :: Description",
				"http://example.org/spring-guides/top-sample-guide",
				"git://example.org/spring-guides/top-sample-guide.git",
				"git@example.org:spring-guides/top-sample-guide.git",
				"https://example.org/spring-guides/top-sample-guide.git",
				Arrays.asList("spring-framework", "spring-boot"));
		GuideResource guideResource = new GuideResource(repository);
		assertThat(guideResource.getName()).isEqualTo("sample-guide");
		assertThat(guideResource.getRepositoryName()).isEqualTo("spring-guides/top-sample-guide");
		assertThat(guideResource.getTitle()).isEqualTo("Title");
		assertThat(guideResource.getDescription()).isEqualTo("Description");
		assertThat(guideResource.getType()).isEqualTo(GuideType.TOPICAL);
		assertThat(guideResource.getProjects()).contains("spring-framework", "spring-boot");
	}

	@Test
	public void deprecatedGuide() {
		Repository repository = new Repository(12L, "deprecated-gs-sample-guide",
				"spring-guides/deprecated-gs-sample-quide",
				"Title :: Description",
				"http://example.org/spring-guides/deprecated-gs-sample-guide",
				"git://example.org/spring-guides/deprecated-gs-sample-guide.git",
				"git@example.org:spring-guides/deprecated-gs-sample-guide.git",
				"https://example.org/spring-guides/deprecated-gs-sample-guide.git",
				Arrays.asList("spring-framework", "spring-boot"));
		GuideResource guideResource = new GuideResource(repository);
		assertThat(guideResource.getName()).isEqualTo("deprecated-gs-sample-guide");
		assertThat(guideResource.getTitle()).isEqualTo("Title");
		assertThat(guideResource.getDescription()).isEqualTo("Description");
		assertThat(guideResource.getType()).isEqualTo(GuideType.UNKNOWN);
		assertThat(guideResource.getProjects()).contains("spring-framework", "spring-boot");
	}

}
