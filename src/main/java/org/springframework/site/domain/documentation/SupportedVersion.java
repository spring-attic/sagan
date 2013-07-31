package org.springframework.site.domain.documentation;

public class SupportedVersion implements Comparable<SupportedVersion> {
	public enum Status {
		CURRENT("Current"), PRERELEASE("Pre-Release"), SUPPORTED("");

		private String displayName;

		private Status(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}

	private String version;
	private Status status;

	public SupportedVersion(String version, Status status) {
		this.version = version;
		this.status = status;
	}

	public boolean isCurrent() {
		return status == Status.CURRENT;
	}

	@Override
	public String toString() {
		return "SupportedVersion{" +
				version + ", status: " + status.name() +
				"}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SupportedVersion version1 = (SupportedVersion) o;

		if (status != version1.status) return false;
		if (!version.equals(version1.version)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = version.hashCode();
		result = 31 * result + status.hashCode();
		return result;
	}

	@Override
	public int compareTo(SupportedVersion other) {
		return this.version.compareTo(other.version);
	}

	public String getFullVersion() {
		return version;
	}

	public String getVersionName() {
		String statusDisplay = "";
		if (status != Status.SUPPORTED) {
			statusDisplay = String.format(" (%s)", status.getDisplayName());
		}
		return version + statusDisplay;
	}

}
