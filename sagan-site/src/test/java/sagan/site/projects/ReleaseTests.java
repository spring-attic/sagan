package sagan.site.projects;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link Release}
 */
public class ReleaseTests {

	@Test
	public void shouldRejectEmptyVersions() {
		assertThatThrownBy(() -> new Release(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Version should not be null");
	}

	@Test
	public void shouldRejectCurrentForNonGAVersions() {
		assertThatThrownBy(() -> new Release(Version.of("1.2.0-SNAPSHOT"), true)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Only Generally Available releases can be marked as current");
	}

	@Test
	public void shouldNotBeCurrentByDefault() {
		Release release = new Release(Version.of("1.2.0"));
		assertThat(release.isCurrent()).isFalse();
	}

	@Test
	public void shouldDeduceRepositoryAndReleaseStatus() {
		Release release = new Release(Version.of("2.3.0"));
		assertThat(release.getRepository()).isEqualTo(Repository.RELEASE);
		assertThat(release.isGeneralAvailability()).isTrue();

		Release milestone = new Release(Version.of("2.3.0-M1"));
		assertThat(milestone.getRepository()).isEqualTo(Repository.MILESTONE);
		assertThat(milestone.isPreRelease()).isTrue();

		Release snapshot = new Release(Version.of("2.3.0-SNAPSHOT"));
		assertThat(snapshot.getRepository()).isEqualTo(Repository.SNAPSHOT);
		assertThat(snapshot.isSnapshot()).isTrue();
	}

	@Test
	public void shouldSortReleasesAccordingToCurrentAndVersion() {
		Release oneOneGa = new Release(Version.of("1.1.0"), true);
		Release oneTwoGa = new Release(Version.of("1.2.0"));
		Release oneTwoM2 = new Release(Version.of("1.2.0-M2"));
		Release oneTwoSnapshot = new Release(Version.of("1.2.0-SNAPSHOT"));
		Release oneThreeGa = new Release(Version.of("1.3.0"));
		List<Release> releases = Stream.of(oneOneGa, oneTwoGa, oneTwoM2, oneTwoSnapshot, oneThreeGa).sorted().collect(Collectors.toList());
		assertThat(releases).containsExactly(oneOneGa, oneThreeGa, oneTwoGa, oneTwoSnapshot, oneTwoM2);
	}

	@Test
	public void shouldExpandDocsUrls() {
		Release release = new Release(Version.of("1.2.0"));
		release.setRefDocUrl("https://example.org/ref/{version}/index.html");
		release.setApiDocUrl("https://example.org/api/{version}/index.html");

		assertThat(release.expandRefDocUrl()).isEqualTo("https://example.org/ref/1.2.0/index.html");
		assertThat(release.expandApiDocUrl()).isEqualTo("https://example.org/api/1.2.0/index.html");
	}

	@Test
	public void shouldExpandDocsUrlsWithCurrentVersion() {
		Release release = new Release(Version.of("1.2.0"), true);
		release.setRefDocUrl("https://example.org/ref/{version}/index.html");
		release.setApiDocUrl("https://example.org/api/{version}/index.html");

		assertThat(release.expandRefDocUrl()).isEqualTo("https://example.org/ref/current/index.html");
		assertThat(release.expandApiDocUrl()).isEqualTo("https://example.org/api/current/index.html");
	}

}