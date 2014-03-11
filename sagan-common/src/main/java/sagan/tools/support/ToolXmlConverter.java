package sagan.tools.support;

import sagan.tools.ToolSuiteDownloads;

public class ToolXmlConverter {

    public ToolSuiteDownloads convert(ToolSuiteXml toolSuiteXml, String toolSuiteName, String shortName) {
        ToolSuiteBuilder state = new ToolSuiteBuilder(shortName);

        for (Release release : toolSuiteXml.getReleases()) {
            if (!release.getName().startsWith(toolSuiteName))
                continue;
            for (Download download : release.getDownloads()) {
                state.addDownload(download);
            }
        }

        return state.build();
    }

}
