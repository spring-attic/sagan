package io.spring.site.domain.tools.toolsuite.parser;

import io.spring.site.domain.tools.toolsuite.ToolSuiteDownloads;
import io.spring.site.domain.tools.toolsuite.xml.Download;
import io.spring.site.domain.tools.toolsuite.xml.Release;
import io.spring.site.domain.tools.toolsuite.xml.ToolSuiteXml;

public class ToolXmlConverter {

    public ToolSuiteDownloads convert(ToolSuiteXml toolSuiteXml, String toolSuiteName, String shortName) {
        ToolSuiteBuilder state = new ToolSuiteBuilder(shortName);

        for (Release release : toolSuiteXml.getReleases()) {
            if (!release.getName().startsWith(toolSuiteName)) continue;
            for (Download download : release.getDownloads()) {
                state.addDownload(download);
            }
        }

        return state.build();
    }

}