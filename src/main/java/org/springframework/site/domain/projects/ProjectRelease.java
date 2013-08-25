package org.springframework.site.domain.projects;

public class ProjectRelease implements Comparable<ProjectRelease> {

	public enum ReleaseStatus {
		CURRENT, PRERELEASE, SUPPORTED;
	}

	private final String versionName;
	private final ReleaseStatus releaseStatus;
	private final String refDocUrl;
	private final String apiDocUrl;
	private final String groupId;
	private final String artifactId;
	private final Repository repository;

	public ProjectRelease(String versionName, ReleaseStatus releaseStatus,
			String refDocUrl, String apiDocUrl, String groupId, String artifactId) {
		this.versionName = versionName;
		this.releaseStatus = releaseStatus;
		this.refDocUrl = refDocUrl;
		this.apiDocUrl = apiDocUrl;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.repository = Repository.get(versionName);
	}

	public boolean isCurrent() {
		return this.releaseStatus == ReleaseStatus.CURRENT;
	}

	public boolean isPreRelease() {
		return this.releaseStatus == ReleaseStatus.PRERELEASE;
	}

	public boolean isSupported() {
		return this.releaseStatus == ReleaseStatus.SUPPORTED;
	}

	public String getVersion() {
		return this.versionName;
	}

	public String getVersionDisplayName() {
		return this.versionName.replaceAll(".RELEASE$", "");
	}

	public String getRefDocUrl() {
		return this.refDocUrl;
	}

	public boolean hasRefDocUrl() {
		return !this.refDocUrl.isEmpty();
	}

	public String getApiDocUrl() {
		return this.apiDocUrl;
	}

	public boolean hasApiDocUrl() {
		return !this.apiDocUrl.isEmpty();
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public Repository getRepository() {
		return this.repository;
	}

	@Override
	public int compareTo(ProjectRelease other) {
		return this.versionName.compareTo(other.versionName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ProjectRelease that = (ProjectRelease) o;

		if (!this.apiDocUrl.equals(that.apiDocUrl))
			return false;
		if (!this.refDocUrl.equals(that.refDocUrl))
			return false;
		if (this.releaseStatus != that.releaseStatus)
			return false;
		if (!this.versionName.equals(that.versionName))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = this.versionName.hashCode();
		result = 31 * result + this.releaseStatus.hashCode();
		result = 31 * result + this.refDocUrl.hashCode();
		result = 31 * result + this.apiDocUrl.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "ProjectRelease{" + "versionName='" + this.versionName + '\''
				+ ", release=" + this.releaseStatus + ", refDocUrl='" + this.refDocUrl
				+ '\'' + ", apiDocUrl='" + this.apiDocUrl + '\'' + '}';
	}

	public static class Repository {
		private String id;
		private String name;
		private String url;
		private Boolean snapshotsEnabled;

		private Repository(String id, String name, String url, Boolean snapshotsEnabled) {
			this.id = id;
			this.name = name;
			this.url = url;
			this.snapshotsEnabled = snapshotsEnabled;
		}

		public static Repository get(String versionName) {
			if (versionName.contains("RELEASE")) {
				return null;
			}

			if (versionName.contains("SNAPSHOT")) {
				return new Repository("spring-snapshots", "Spring Snapshots",
						"http://repo.springsource.org/snapshot", true);
			}

			return new Repository("spring-milestones", "Spring Milestones",
					"http://repo.springsource.org/milestone", false);
		}

		public String getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public String getUrl() {
			return this.url;
		}

		public Boolean getSnapshotsEnabled() {
			return this.snapshotsEnabled;
		}
	}
}
