package org.springframework.site.domain.documentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.springframework.site.domain.documentation.SupportedVersion.Release.CURRENT;
import static org.springframework.site.domain.documentation.SupportedVersion.Release.PRERELEASE;
import static org.springframework.site.domain.documentation.SupportedVersion.Release.SUPPORTED;

public class SupportedVersions implements Iterable<SupportedVersion> {

	private List<SupportedVersion> versions;

	public static SupportedVersions build(List<String> versionStrings) {
		List<SupportedVersion> versions = new ArrayList<>();

		Collections.sort(versionStrings);
		Collections.reverse(versionStrings);

		String currentVersion = null;
		for (String versionString : versionStrings) {
			boolean isPreRelease = versionString.matches("[0-9.]+(M|RC)\\d+");
			SupportedVersion.Release release = SUPPORTED;
			if (isPreRelease) {
				release = PRERELEASE;
			} else if(currentVersion == null) {
				currentVersion = versionString;
				release = CURRENT;
			}
			versions.add(new SupportedVersion(versionString, release));
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
