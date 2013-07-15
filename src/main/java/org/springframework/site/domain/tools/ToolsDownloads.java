package org.springframework.site.domain.tools;

import java.util.Map;

public interface ToolsDownloads {
	public interface ToolsPlatform {}

	public Map<String, ? extends ToolsPlatform> getPlatforms();
}
