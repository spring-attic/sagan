package org.springframework.site.domain.tools.toolsuite;

import org.springframework.site.domain.tools.ToolsDownloads;

import java.util.List;

import static org.springframework.util.StringUtils.capitalize;

public class ToolSuitePlatform implements ToolsDownloads.ToolsPlatform {
	private String name;
	private List<EclipseVersion> eclipseVersions;
	private String releaseName;

	public ToolSuitePlatform(String name, String releaseName, List<EclipseVersion> eclipseVersions) {
		this.name = capitalize(name);
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
