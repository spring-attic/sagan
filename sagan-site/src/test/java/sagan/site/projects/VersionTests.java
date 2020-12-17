package sagan.site.projects;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link Version}.
 */
public class VersionTests {

	@Test
	public void shouldFailWithEmptyVersion() {
		assertThatThrownBy(() -> Version.of("  ")).isInstanceOf(InvalidVersionException.class)
				.hasMessage("Version string should not be empty");
	}

	@Test
	public void shouldFailWithVersionStartingWithSeparator() {
		assertThatThrownBy(() -> Version.of("-2.3.0")).isInstanceOf(InvalidVersionException.class)
				.hasMessage("Version cannot start with a '.' or '-' separator");
	}

	@Test
	public void shouldFailWithVersionContainingNonAlphanumChars() {
		assertThatThrownBy(() -> Version.of("Horsham.SR10 [3.0.10.RELEASE]")).isInstanceOf(InvalidVersionException.class)
				.hasMessage("Character ' ' is not alphanumeric");
	}

	@Test
	public void shouldOrderVersionSchemeWithNumbers() {
		List<String> sortedVersions = Stream.of("2.3.0.BUILD-SNAPSHOT", "2.3.0.M1", "2.3.0.M2", "2.3.0.RC1",
				"2.3.0.RC2", "2.3.0.RELEASE")
				.map(Version::of)
				.sorted()
				.map(Version::toString)
				.collect(Collectors.toList());

		assertThat(sortedVersions).containsExactly("2.3.0.BUILD-SNAPSHOT", "2.3.0.M1", "2.3.0.M2", "2.3.0.RC1",
				"2.3.0.RC2", "2.3.0.RELEASE");
	}

	@Test
	public void shouldOrderVersionSchemeWithReleaseTrains() {
		List<String> sortedVersions = Stream.of("Hopper-BUILD-SNAPSHOT", "Hopper-M1", "Hopper-RC1", "Hopper-RELEASE",
				"Hopper-SR2", "Hopper-SR1")
				.map(Version::of)
				.sorted()
				.map(Version::toString)
				.collect(Collectors.toList());

		assertThat(sortedVersions).containsExactly("Hopper-BUILD-SNAPSHOT", "Hopper-M1", "Hopper-RC1", "Hopper-RELEASE",
				"Hopper-SR1", "Hopper-SR2");
	}

	@Test
	public void shouldOrderNewVersionSchemeWithNumbers() {
		List<String> sortedVersions = Stream.of("2.4.0-SNAPSHOT", "2.3.1", "2.3.1-SNAPSHOT", "2.3.0",
				"2.3.0-SNAPSHOT", "2.3.0-RC2", "2.3.0-RC1", "2.3.0-M2", "2.3.0-M1")
				.map(Version::of)
				.sorted()
				.map(Version::toString)
				.collect(Collectors.toList());

		assertThat(sortedVersions).containsExactly("2.3.0-M1", "2.3.0-M2", "2.3.0-RC1", "2.3.0-RC2", "2.3.0-SNAPSHOT",
				"2.3.0", "2.3.1-SNAPSHOT", "2.3.1", "2.4.0-SNAPSHOT");
	}

	@Test
	public void shouldOrderNewVersionSchemeWithYears() {
		List<String> sortedVersions = Stream.of("2020.1.0-SNAPSHOT", "2020.0.1", "2020.0.1-SNAPSHOT",
				"2020.0.0", "2020.0.0-SNAPSHOT", "2020.0.0-RC1", "2020.0.0-M2", "2020.0.0-M1")
				.map(Version::of)
				.sorted()
				.map(Version::toString)
				.collect(Collectors.toList());
		assertThat(sortedVersions).containsExactly(
				"2020.0.0-M1", "2020.0.0-M2", "2020.0.0-RC1", "2020.0.0-SNAPSHOT", "2020.0.0",
				"2020.0.1-SNAPSHOT", "2020.0.1", "2020.1.0-SNAPSHOT");
	}
}
