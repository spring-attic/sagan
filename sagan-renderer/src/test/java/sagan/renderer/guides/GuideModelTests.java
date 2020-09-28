package sagan.renderer.guides;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import sagan.renderer.github.Repository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GuideModel}
 */
public class GuideModelTests {

	@Test
	public void nullRepositoryDescription() {
		Repository repository = new Repository(12L, "gs-sample-guide",
				"spring-guides/gs-sample-guide", null,
				"http://example.org/spring-guides/gs-sample-guide",
				"git://example.org/spring-guides/gs-sample-guide.git",
				"git@example.org:spring-guides/gs-sample-guide.git",
				"https://example.org/spring-guides/gs-sample-guide.git",
				null);
		GuideModel guideModel = new GuideModel(repository);
		assertThat(guideModel.getName()).isEqualTo("sample-guide");
		assertThat(guideModel.getRepositoryName()).isEqualTo("spring-guides/gs-sample-guide");
		assertThat(guideModel.getTitle()).isEmpty();
		assertThat(guideModel.getDescription()).isEmpty();
		assertThat(guideModel.getType()).isEqualTo(GuideType.GETTING_STARTED);
		assertThat(guideModel.getGithubUrl()).isEqualTo("http://example.org/spring-guides/gs-sample-guide");
		assertThat(guideModel.getCloneUrl()).isEqualTo("https://example.org/spring-guides/gs-sample-guide.git");
		assertThat(guideModel.getGitUrl()).isEqualTo("git://example.org/spring-guides/gs-sample-guide.git");
		assertThat(guideModel.getSshUrl()).isEqualTo("git@example.org:spring-guides/gs-sample-guide.git");
		assertThat(guideModel.getProjects()).isEmpty();
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
		GuideModel guideModel = new GuideModel(repository);
		assertThat(guideModel.getName()).isEqualTo("sample-guide");
		assertThat(guideModel.getRepositoryName()).isEqualTo("spring-guides/tut-sample-guide");
		assertThat(guideModel.getTitle()).isEqualTo("Title");
		assertThat(guideModel.getDescription()).isEqualTo("Description");
		assertThat(guideModel.getType()).isEqualTo(GuideType.TUTORIAL);
		assertThat(guideModel.getProjects()).isEmpty();
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
		GuideModel guideModel = new GuideModel(repository);
		assertThat(guideModel.getName()).isEqualTo("sample-guide");
		assertThat(guideModel.getRepositoryName()).isEqualTo("spring-guides/top-sample-guide");
		assertThat(guideModel.getTitle()).isEqualTo("Title");
		assertThat(guideModel.getDescription()).isEqualTo("Description");
		assertThat(guideModel.getType()).isEqualTo(GuideType.TOPICAL);
		assertThat(guideModel.getProjects()).contains("spring-framework", "spring-boot");
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
		GuideModel guideModel = new GuideModel(repository);
		assertThat(guideModel.getName()).isEqualTo("deprecated-gs-sample-guide");
		assertThat(guideModel.getTitle()).isEqualTo("Title");
		assertThat(guideModel.getDescription()).isEqualTo("Description");
		assertThat(guideModel.getType()).isEqualTo(GuideType.UNKNOWN);
		assertThat(guideModel.getProjects()).contains("spring-framework", "spring-boot");
	}

}
