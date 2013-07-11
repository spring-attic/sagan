package org.springframework.site.domain.tools.toolsuite;

import java.util.List;
import java.util.Map;

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
}
