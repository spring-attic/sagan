package org.springframework.site.domain.tools.eclipse;

import org.springframework.site.domain.tools.ToolsDownloads;

import java.util.Map;

public class EclipseDownloads implements ToolsDownloads {
	private final Map<String, EclipsePlatform> platforms;

	public EclipseDownloads(Map<String, EclipsePlatform> platforms) {
		this.platforms = platforms;
	}

	public Map<String, EclipsePlatform> getPlatforms() {
		return platforms;
	}
}
