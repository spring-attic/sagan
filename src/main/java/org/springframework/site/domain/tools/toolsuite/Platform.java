package org.springframework.site.domain.tools.toolsuite;

import java.util.List;

public class Platform {
	private String name;
	private List<EclipseVersion> eclipseVersions;
	private String releaseName;

	public Platform(String name, String releaseName, List<EclipseVersion> eclipseVersions) {
		this.name = name;
		this.releaseName = releaseName;
		this.eclipseVersions = eclipseVersions;
	}

	public String getName() {
		return name;
	}

	public List<EclipseVersion> getEclipseVersions() {
		return eclipseVersions;
	}

	public String getReleaseName() {
		return releaseName;
	}
}
