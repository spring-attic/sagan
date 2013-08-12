package org.springframework.site.domain.projects;

public class ProjectRelease implements Comparable<ProjectRelease>{

	public enum ReleaseStatus {
		CURRENT, PRERELEASE, SUPPORTED;
	}

	private final String versionName;
	private final ReleaseStatus releaseStatus;
	private final String refDocUrl;
	private final String apiDocUrl;

    public ProjectRelease(String versionName, ReleaseStatus releaseStatus, String refDocUrl, String apiDocUrl) {
        this.versionName = versionName;
		this.releaseStatus = releaseStatus;
		this.refDocUrl = refDocUrl;
		this.apiDocUrl = apiDocUrl;
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

}
