package org.springframework.site.domain.tools.eclipse.parser;

import org.springframework.site.domain.tools.eclipse.EclipseDownloads;
import org.springframework.site.domain.tools.eclipse.EclipsePackage;
import org.springframework.site.domain.tools.eclipse.EclipsePlatform;
import org.springframework.site.domain.tools.eclipse.EclipseRelease;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlDownload;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlPackage;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlProduct;
import org.springframework.site.domain.tools.toolsuite.Architecture;
import org.springframework.site.domain.tools.toolsuite.DownloadLink;
import org.springframework.site.domain.tools.toolsuite.parser.DownloadLinkExtractor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EclipseDownloadsBuilder {
	private final Map<String, EclipsePlatform> platforms = new HashMap<>();
	private final Map<String, EclipsePackage> packages = new HashMap<>();
	private final Map<String, EclipseRelease> releases = new HashMap<>();
	private final Map<String, Architecture> architectureMap = new LinkedHashMap<>();

	private final DownloadLinkExtractor downloadLinkExtractor = new DownloadLinkExtractor();

	public void addDownload(EclipseXmlDownload eclipseXmlDownload, EclipseXmlPackage eclipseXmlPackage, EclipseXmlProduct eclipseXmlProduct) {
		String os = eclipseXmlDownload.getOs();
		EclipsePlatform platform = getEclipsePlatform(os);
		EclipseRelease release = getEclipseRelease(eclipseXmlProduct, platform);
		EclipsePackage eclipsePackage = getEclipsePackage(eclipseXmlPackage, os, release);
		Architecture architecture = getArchitecture(eclipseXmlDownload, os, release, eclipsePackage);
		architecture.getDownloadLinks().add(downloadLinkExtractor.createDownloadLink(eclipseXmlDownload));
		platforms.put(os, platform);
	}

	private Architecture getArchitecture(EclipseXmlDownload eclipseXmlDownload, String os, EclipseRelease release, EclipsePackage eclipsePackage) {
		String key = os + release.getName() + eclipsePackage.getName() + eclipseXmlDownload.getDescription();
		Architecture architecture = architectureMap.get(key);
		if (architecture == null) {
			String name = eclipseXmlDownload.getDescription();
			architecture = new Architecture(name, new ArrayList<DownloadLink>());

			architectureMap.put(key, architecture);
			eclipsePackage.getArchitectures().add(architecture);
		}

		return architecture;
	}

	private EclipsePlatform getEclipsePlatform(String os) {
		String key = os;
		EclipsePlatform platform = platforms.get(key);
		if (platform == null) {
			platform = new EclipsePlatform(StringUtils.capitalize(os), new ArrayList<EclipseRelease>());
			platforms.put(key, platform);
		}
		return platform;
	}

	private EclipseRelease getEclipseRelease(EclipseXmlProduct eclipseXmlProduct, EclipsePlatform platform) {
		String key = platform.getName() + eclipseXmlProduct.getName();
		EclipseRelease release = releases.get(key);
		if (release == null) {
			String name = new String(eclipseXmlProduct.getName()).replaceAll("(.+) Package Downloads.*", "$1");
			String version = new String(eclipseXmlProduct.getName()).replaceAll(".*\\(based on (.*)\\)", "$1");
			release = new EclipseRelease(name, version, new ArrayList<EclipsePackage>());
			releases.put(key, release);
			platform.getReleases().add(release);
		}
		return release;
	}

	private EclipsePackage getEclipsePackage(EclipseXmlPackage eclipseXmlPackage, String os, EclipseRelease release) {
		String key = os + release.getName() + eclipseXmlPackage.getName();
		EclipsePackage eclipsePackage = packages.get(key);
		if (eclipsePackage == null) {
			String name = new String(eclipseXmlPackage.getName()).replaceAll(" \\(.*\\)", "");
			eclipsePackage = new EclipsePackage(name, new ArrayList<Architecture>());
			packages.put(key, eclipsePackage);
			release.getPackages().add(eclipsePackage);
		}

		return eclipsePackage;
	}

	public EclipseDownloads build() {
		return new EclipseDownloads(platforms);
	}
}
