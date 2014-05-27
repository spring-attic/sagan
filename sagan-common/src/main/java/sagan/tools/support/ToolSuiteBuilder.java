package sagan.tools.support;

import sagan.tools.Architecture;
import sagan.tools.Download;
import sagan.tools.DownloadLink;
import sagan.tools.EclipseVersion;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.ToolSuitePlatform;
import sagan.tools.UpdateSiteArchive;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ToolSuiteBuilder {

    private final DownloadLinkExtractor downloadLinkExtractor = new DownloadLinkExtractor();
    private final String shortName;

    private Map<String, ToolSuitePlatform> platformMap = new LinkedHashMap<>();
    private List<UpdateSiteArchive> updateSiteArchives = new ArrayList<>();
    private Map<String, EclipseVersion> eclipseVersionMap = new LinkedHashMap<>();
    private Map<String, Architecture> architectureMap = new LinkedHashMap<>();
    private String releaseName;
    private String whatsNew;

    public ToolSuiteBuilder(String shortName) {
        this.shortName = shortName;
    }

    public void setWhatsNew(String whatsNew) {
        this.whatsNew = whatsNew;
    }

    public void addDownload(Download download) {
        if (download.getOs().equals("all")) {
            extractArchive(download);
        } else {
            if (releaseName == null) {
                releaseName = download.getVersion();
            }
            extractPlatformDownloadLink(download);
        }
    }

    private void extractPlatformDownloadLink(Download download) {
        ToolSuitePlatform platform = createOrFindPlatform(download.getOs(), download.getVersion());
        EclipseVersion eclipseVersion = createOrFindEclipseVersion(download.getEclipseVersion(), platform);
        Architecture architecture = createOrFindArchitecture(download.getDescription(), eclipseVersion, platform);

        DownloadLink link = downloadLinkExtractor.createDownloadLink(download);
        architecture.getDownloadLinks().add(link);
    }

    private void extractArchive(Download download) {
        String url = download.getBucket() + download.getFile();
        UpdateSiteArchive archive = new UpdateSiteArchive(download.getEclipseVersion(), url, download.getSize());
        if (!updateSiteArchives.contains(archive)) {
            updateSiteArchives.add(archive);
        }
    }

    public ToolSuiteDownloads build() {
        return new ToolSuiteDownloads(shortName, releaseName, whatsNew, platformMap, updateSiteArchives);
    }

    private ToolSuitePlatform createOrFindPlatform(String os, String name) {
        ToolSuitePlatform platform = platformMap.get(os);
        if (platform == null) {
            platform = new ToolSuitePlatform(os, new ArrayList<>());
            platformMap.put(os, platform);
        }
        return platform;
    }

    private EclipseVersion createOrFindEclipseVersion(String eclipseVersionName, ToolSuitePlatform platform) {
        String key = platform.getName() + eclipseVersionName;

        EclipseVersion eclipseVersion = eclipseVersionMap.get(key);
        if (eclipseVersion == null) {
            eclipseVersion = new EclipseVersion(eclipseVersionName, new ArrayList<>());
            platform.getEclipseVersions().add(eclipseVersion);
            eclipseVersionMap.put(key, eclipseVersion);
        }
        return eclipseVersion;
    }

    private Architecture createOrFindArchitecture(String architectureName, EclipseVersion eclipseVersion,
                                                  ToolSuitePlatform platform) {
        String key = platform.getName() + eclipseVersion.getName() + architectureName;

        Architecture architecture = architectureMap.get(key);
        if (architecture == null) {
            architecture = new Architecture(architectureName, new ArrayList<>());
            eclipseVersion.getArchitectures().add(architecture);
            architectureMap.put(key, architecture);
        }
        return architecture;
    }
}
