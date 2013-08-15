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

	public ProjectRelease(String versionName, ReleaseStatus releaseStatus, String refDocUrl, String apiDocUrl, String groupId, String artifactId) {
		this.versionName = versionName;
		this.releaseStatus = releaseStatus;
		this.refDocUrl = refDocUrl;
		this.apiDocUrl = apiDocUrl;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.repository = Repository.get(versionName);
	}

	public boolean isCurrent() {
		return releaseStatus == ReleaseStatus.CURRENT;
	}

	public boolean isPreRelease() {
		return releaseStatus == ReleaseStatus.PRERELEASE;
	}

	public boolean isSupported() {
		return releaseStatus == ReleaseStatus.SUPPORTED;
	}

	public String getFullName() {
		return versionName;
	}

	public String getShortName() {
		return versionName.replaceAll(".RELEASE$", "");
	}

	public String getRefDocUrl() {
		return refDocUrl;
	}

	public boolean hasRefDocUrl() {
		return !refDocUrl.isEmpty();
	}

	public String getApiDocUrl() {
		return apiDocUrl;
	}

	public boolean hasApiDocUrl() {
		return !apiDocUrl.isEmpty();
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public Repository getRepository() {
		return repository;
	}

	@Override
	public int compareTo(ProjectRelease other) {
		return this.versionName.compareTo(other.versionName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProjectRelease that = (ProjectRelease) o;

		if (!apiDocUrl.equals(that.apiDocUrl)) return false;
		if (!refDocUrl.equals(that.refDocUrl)) return false;
		if (releaseStatus != that.releaseStatus) return false;
		if (!versionName.equals(that.versionName)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = versionName.hashCode();
		result = 31 * result + releaseStatus.hashCode();
		result = 31 * result + refDocUrl.hashCode();
		result = 31 * result + apiDocUrl.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "ProjectRelease{" +
				"versionName='" + versionName + '\'' +
				", release=" + releaseStatus +
				", refDocUrl='" + refDocUrl + '\'' +
				", apiDocUrl='" + apiDocUrl + '\'' +
				'}';
	}

	private static class Repository {
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
				return new Repository(
						"spring-snapshots",
						"Spring Snapshots",
						"http://repo.springsource.org/snapshot",
						true
				);
			}

			return new Repository(
					"spring-milestones",
					"Spring Milestones",
					"http://repo.springsource.org/milestone",
					false
			);
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getUrl() {
			return url;
		}

		public Boolean getSnapshotsEnabled() {
			return snapshotsEnabled;
		}
	}
}
