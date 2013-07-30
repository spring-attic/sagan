package org.springframework.site.domain.tools.toolsuite.parser;

import org.springframework.site.domain.tools.toolsuite.Architecture;
import org.springframework.site.domain.tools.toolsuite.DownloadLink;
import org.springframework.site.domain.tools.toolsuite.EclipseVersion;
import org.springframework.site.domain.tools.toolsuite.ToolSuiteDownloads;
import org.springframework.site.domain.tools.toolsuite.ToolSuitePlatform;
import org.springframework.site.domain.tools.toolsuite.UpdateSiteArchive;
import org.springframework.site.domain.tools.toolsuite.xml.Download;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ToolSuiteBuilder {

	private final DownloadLinkExtractor downloadLinkExtractor = new DownloadLinkExtractor();
	private Map<String, ToolSuitePlatform> platformMap = new LinkedHashMap<String, ToolSuitePlatform>();
	private List<UpdateSiteArchive> updateSiteArchives = new ArrayList<UpdateSiteArchive>();
	private Map<String, EclipseVersion> eclipseVersionMap = new LinkedHashMap<String, EclipseVersion>();
	private Map<String, Architecture> architectureMap = new LinkedHashMap<String, Architecture>();
	private String releaseName;

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
		return new ToolSuiteDownloads(releaseName, platformMap, updateSiteArchives);
	}

	private ToolSuitePlatform createOrFindPlatform(String os, String name) {
		ToolSuitePlatform platform = platformMap.get(os);
		if (platform == null) {
			platform = new ToolSuitePlatform(os, new ArrayList<EclipseVersion>());
			platformMap.put(os, platform);
		}
		return platform;
	}

	private EclipseVersion createOrFindEclipseVersion(String eclipseVersionName, ToolSuitePlatform platform) {
		String key = platform.getName() + eclipseVersionName;

		EclipseVersion eclipseVersion = eclipseVersionMap.get(key);
		if (eclipseVersion == null) {
			eclipseVersion = new EclipseVersion(eclipseVersionName, new ArrayList<Architecture>());
			platform.getEclipseVersions().add(eclipseVersion);
			eclipseVersionMap.put(key, eclipseVersion);
		}
		return eclipseVersion;
	}

	private Architecture createOrFindArchitecture(String architectureName, EclipseVersion eclipseVersion, ToolSuitePlatform platform) {
		String key = platform.getName() + eclipseVersion.getName() + architectureName;

		Architecture architecture = architectureMap.get(key);
		if (architecture == null) {
			architecture = new Architecture(architectureName, new ArrayList<DownloadLink>());
			eclipseVersion.getArchitectures().add(architecture);
			architectureMap.put(key, architecture);
		}
		return architecture;
	}
}