package org.springframework.site.domain.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.springframework.site.domain.projects.Version.Release.CURRENT;
import static org.springframework.site.domain.projects.Version.Release.PRERELEASE;
import static org.springframework.site.domain.projects.Version.Release.SUPPORTED;

public class SupportedVersions implements Iterable<Version> {

	private List<Version> versions;

	public static SupportedVersions build(List<String> versionStrings) {
		List<Version> versions = new ArrayList<>();

		Collections.sort(versionStrings);
		Collections.reverse(versionStrings);

		String currentVersion = null;
		for (String versionString : versionStrings) {
			boolean isPreRelease = versionString.matches("[0-9.]+(M|RC)\\d+");
			Version.Release release = SUPPORTED;
			if (isPreRelease) {
				release = PRERELEASE;
			} else if(currentVersion == null) {
				currentVersion = versionString;
				release = CURRENT;
			}
			versions.add(new Version(versionString, release));
		}

		return new SupportedVersions(versions);
	}

	public SupportedVersions(List<Version> versions) {
		this.versions = versions;
	}

	public int size() {
		return versions.size();
	}

	@Override
	public Iterator<Version> iterator() {
		return versions.iterator();
	}

	public Version getCurrent() {
		for (Version version : this) {
			if (version.isCurrent()) {
				return version;
			}
		}
		return null;
	}
}
