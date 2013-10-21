package sagan.tools.toolsuite.parser;

import sagan.tools.toolsuite.ToolSuiteDownloads;
import sagan.tools.toolsuite.xml.Download;
import sagan.tools.toolsuite.xml.Release;
import sagan.tools.toolsuite.xml.ToolSuiteXml;

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