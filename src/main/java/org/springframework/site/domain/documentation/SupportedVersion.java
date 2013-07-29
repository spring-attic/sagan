package org.springframework.site.domain.documentation;

public class SupportedVersion {
	private String version;
	private boolean current;

	public SupportedVersion(String version, boolean current) {
		this.version = version;
		this.current = current;
	}

	public boolean isCurrent() {
		return current;
	}

	@Override
	public String toString() {
		return "SupportedVersion{" +
				version + (current ? ", current" : "") +
				"}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SupportedVersion that = (SupportedVersion) o;

		if (current != that.current) return false;
		if (!version.equals(that.version)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = version.hashCode();
		result = 31 * result + (current ? 1 : 0);
		return result;
	}

	public String getFullVersion() {
		return version;
	}
}
