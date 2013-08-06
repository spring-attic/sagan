package org.springframework.site.domain.projects;

public class ProjectVersion implements Comparable<ProjectVersion>{

	public enum Release {
		CURRENT, PRERELEASE, SUPPORTED;
	}

	private final String versionName;
	private final Release release;
	private final String refDocUrl;
	private final String apiDocUrl;

    public ProjectVersion(String versionName, Release release, String refDocUrl, String apiDocUrl) {
        this.versionName = versionName;
		this.release = release;
		this.refDocUrl = refDocUrl;
		this.apiDocUrl = apiDocUrl;
    }

	public boolean isCurrent() {
		return release == Release.CURRENT;
	}

	public boolean isPreRelease() {
		return release == Release.PRERELEASE;
	}

	public boolean isSupported() {
		return release == Release.SUPPORTED;
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
	public int compareTo(ProjectVersion other) {
		return this.versionName.compareTo(other.versionName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProjectVersion that = (ProjectVersion) o;

		if (!apiDocUrl.equals(that.apiDocUrl)) return false;
		if (!refDocUrl.equals(that.refDocUrl)) return false;
		if (release != that.release) return false;
		if (!versionName.equals(that.versionName)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = versionName.hashCode();
		result = 31 * result + release.hashCode();
		result = 31 * result + refDocUrl.hashCode();
		result = 31 * result + apiDocUrl.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "ProjectVersion{" +
				"versionName='" + versionName + '\'' +
				", release=" + release +
				", refDocUrl='" + refDocUrl + '\'' +
				", apiDocUrl='" + apiDocUrl + '\'' +
				'}';
	}

}
