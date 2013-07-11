package org.springframework.site.domain.tools.toolsuite;

import java.util.Map;

public class ToolSuite {

	private Map<String, Platform> platforms;

	public ToolSuite(Map<String, Platform> platforms) {
		this.platforms = platforms;
	}

	public Map<String, Platform> getPlatforms() {
		return platforms;
	}
}
