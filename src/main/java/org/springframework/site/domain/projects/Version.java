package org.springframework.site.domain.projects;

public class Version implements Comparable<Version> {
	public enum Release {
		CURRENT("Current"), PRERELEASE("Pre-Release"), SUPPORTED("");

		private String displayName;

		private Release(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}

	private String version;
	private Release release;

	public Version(String version, Release release) {
		this.version = version;
		this.release = release;
	}

	public boolean isCurrent() {
		return release == Release.CURRENT;
	}

	@Override
	public String toString() {
		return "Version{" +
				version + ", release: " + release.name() +
				"}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Version version1 = (Version) o;

		if (release != version1.release) return false;
		if (!version.equals(version1.version)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = version.hashCode();
		result = 31 * result + release.hashCode();
		return result;
	}

	@Override
	public int compareTo(Version other) {
		return this.version.compareTo(other.version);
	}

	public String getFullVersion() {
		return version;
	}

	public String getVersionName() {
		String statusDisplay = "";
		if (release != Release.SUPPORTED) {
			statusDisplay = String.format(" (%s)", release.getDisplayName());
		}
		return version + statusDisplay;
	}

	public String getShortName() {
		return version.replaceAll(".RELEASE$", "");
	}

	public String getReleaseDisplayName() {
		return release.getDisplayName();
	}
}
