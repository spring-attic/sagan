package org.springframework.site.domain.tools.toolsuite;

import org.springframework.site.domain.tools.ToolsDownloads;

import java.util.List;
import java.util.Map;

public class ToolSuiteDownloads implements ToolsDownloads {

	private Map<String, Platform> platforms;
	private List<UpdateSiteArchive> archives;

	public ToolSuiteDownloads(Map<String, Platform> platforms, List<UpdateSiteArchive> archives) {
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
