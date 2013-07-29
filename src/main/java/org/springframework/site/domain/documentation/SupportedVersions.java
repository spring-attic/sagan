package org.springframework.site.domain.documentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SupportedVersions implements Iterable<SupportedVersion> {

	private List<SupportedVersion> versions;

	public static SupportedVersions build(List<String> versionStrings) {
		List<SupportedVersion> versions = new ArrayList<>();

		String currentVersion = "";
		for (String versionString : versionStrings) {
			boolean isPreRelease = versionString.matches("[0-9.]+M\\d+");
			boolean greaterThanCurrent = versionString.compareToIgnoreCase(currentVersion) > 0;
			if (!isPreRelease && greaterThanCurrent) {
				currentVersion = versionString;
			}
		}

		for (String versionString : versionStrings) {
			versions.add(new SupportedVersion(versionString, versionString.equals(currentVersion)));
		}
		return new SupportedVersions(versions);
	}

	public SupportedVersions(List<SupportedVersion> versions) {
		this.versions = versions;
	}

	public int size() {
		return versions.size();
	}

	@Override
	public Iterator<SupportedVersion> iterator() {
		return versions.iterator();
	}

	public SupportedVersion getCurrent() {
		for (SupportedVersion version : this) {
			if (version.isCurrent()) {
				return version;
			}
		}
		return null;
	}
}
