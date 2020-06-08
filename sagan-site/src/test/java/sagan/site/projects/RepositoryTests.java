package sagan.site.projects;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Repository}
 */
public class RepositoryTests {

	@Test
	public void shouldReturnRepositoryForReleaseStatus() {
		assertThat(Repository.of(ReleaseStatus.GENERAL_AVAILABILITY)).isEqualTo(Repository.RELEASE);
		assertThat(Repository.of(ReleaseStatus.PRERELEASE)).isEqualTo(Repository.MILESTONE);
		assertThat(Repository.of(ReleaseStatus.SNAPSHOT)).isEqualTo(Repository.SNAPSHOT);
	}

	@Test
	public void shouldProvideInfoForReleaseRepository() {
		assertThat(Repository.RELEASE.getId()).isEqualTo("spring-releases");
		assertThat(Repository.RELEASE.getName()).isEqualTo("Spring Releases");
		assertThat(Repository.RELEASE.getUrl()).isEqualTo("https://repo.spring.io/libs-release");
		assertThat(Repository.RELEASE.isSnapshotsEnabled()).isEqualTo(false);
	}

	@Test
	public void shouldProvideInfoForMilestoneRepository() {
		assertThat(Repository.MILESTONE.getId()).isEqualTo("spring-milestones");
		assertThat(Repository.MILESTONE.getName()).isEqualTo("Spring Milestones");
		assertThat(Repository.MILESTONE.getUrl()).isEqualTo("https://repo.spring.io/libs-milestone");
		assertThat(Repository.MILESTONE.isSnapshotsEnabled()).isEqualTo(false);
	}

	@Test
	public void shouldProvideInfoForSnapshotRepository() {
		assertThat(Repository.SNAPSHOT.getId()).isEqualTo("spring-snapshots");
		assertThat(Repository.SNAPSHOT.getName()).isEqualTo("Spring Snapshots");
		assertThat(Repository.SNAPSHOT.getUrl()).isEqualTo("https://repo.spring.io/libs-snapshot");
		assertThat(Repository.SNAPSHOT.isSnapshotsEnabled()).isEqualTo(true);
	}

	@Test
	public void shouldUseSecureUrls() {
		for (Repository repository : Repository.values()) {
			assertThat(repository.getUrl()).startsWith("https://");
		}
	}
}
