package org.springframework.site.domain.tools.parser;

import org.springframework.site.domain.tools.toolsuite.ToolSuite;
import org.springframework.site.domain.tools.xml.Download;
import org.springframework.site.domain.tools.xml.Release;
import org.springframework.site.domain.tools.xml.ToolSuiteXml;

public class ToolXmlConverter {

	public ToolSuite convert(ToolSuiteXml toolSuiteXml, String toolSuiteName) {
		ToolSuiteBuilder state = new ToolSuiteBuilder();

		for (Release release : toolSuiteXml.getReleases()) {
			if (!release.getName().startsWith(toolSuiteName)) continue;
			for (Download download : release.getDownloads()) {
				state.addDownload(download);
			}
		}

		return state.build();
	}

}