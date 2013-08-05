package org.springframework.site.domain.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.site.domain.projects.Version.Release.CURRENT;
import static org.springframework.site.domain.projects.Version.Release.PRERELEASE;
import static org.springframework.site.domain.projects.Version.Release.SUPPORTED;

public class SupportedVersions {

	public static List<Version> assignVersions(List<String> versionStrings) {
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

		return versions;
	}
}
