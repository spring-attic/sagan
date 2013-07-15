package org.springframework.site.domain.tools.toolsuite;

import java.util.*;

public class ToolSuite {

	private Map<String, Platform> platforms;
	private List<UpdateSiteArchive> archives;

	public ToolSuite(Map<String, Platform> platforms, List<UpdateSiteArchive> archives) {
		this.platforms = platforms;
		this.archives = archives;
	}

	public Map<String, Platform> getPlatforms() {
		return platforms;
	}

	public List<UpdateSiteArchive> getArchives() {
		return archives;
	}

	public Set<DownloadLink> getPreferredDownloadLinks() {
		Set<DownloadLink> links = new HashSet<>();
		addLinks(links, "windows", "exe");
		addLinks(links, "windows", "exe");
		addLinks(links, "mac", "dmg");
		addLinks(links, "mac", "dmg");
		addLinks(links, "linux", "tar.gz");
		addLinks(links, "linux", "tar.gz");
		return links;
	}

	private void addLinks(Set<DownloadLink> links, String platformString, String fileType) {
		Platform platform = platforms.get(platformString);
		if (platform == null) return;

		EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
		for (Architecture architecture : eclipseVersion.getArchitectures()) {
			for (DownloadLink link : architecture.getDownloadLinks()) {
				if (link.getFileType().equals(fileType)) {
					links.add(link);
				}
			}
		}
	}

	public String getPreferredVersion() {
		for (Platform platform : platforms.values()) {
			return platform.getReleaseName();
		}
		return "unknown";
	}
}
