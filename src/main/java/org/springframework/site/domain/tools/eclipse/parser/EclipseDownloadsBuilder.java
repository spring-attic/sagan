package org.springframework.site.domain.tools.eclipse.parser;

import org.springframework.site.domain.tools.eclipse.EclipseDownloadLink;
import org.springframework.site.domain.tools.eclipse.EclipseDownloads;
import org.springframework.site.domain.tools.eclipse.EclipsePackage;
import org.springframework.site.domain.tools.eclipse.EclipsePlatform;
import org.springframework.site.domain.tools.eclipse.EclipseRelease;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlDownload;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlPackage;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlProduct;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EclipseDownloadsBuilder {
	private final Map<String, EclipsePlatform> platforms = new HashMap<>();
	private final Map<String, EclipsePackage> packages = new HashMap<>();
	private final Map<String, EclipseRelease> releases = new HashMap<>();

	public void addDownload(EclipseXmlDownload eclipseXmlDownload, EclipseXmlPackage eclipseXmlPackage, EclipseXmlProduct eclipseXmlProduct) {
		String os = eclipseXmlDownload.getOs();
		EclipsePlatform platform = getEclipsePlatform(os);
		EclipseRelease release = getEclipseRelease(eclipseXmlProduct, platform);
		EclipsePackage eclipsePackage = getEclipsePackage(eclipseXmlPackage, os, release);
		EclipseDownloadLink download = new EclipseDownloadLink(eclipseXmlDownload.getDescription(), eclipseXmlDownload.getBucket() + eclipseXmlDownload.getFile());
		eclipsePackage.getDownloadLinks().add(download);
		platforms.put(os, platform);
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
			release = new EclipseRelease(eclipseXmlProduct.getName(), new ArrayList<EclipsePackage>());
			releases.put(key, release);
			platform.getReleases().add(release);
		}
		return release;
	}

	private EclipsePackage getEclipsePackage(EclipseXmlPackage eclipseXmlPackage, String os, EclipseRelease release) {
		String key = os + release.getName() + eclipseXmlPackage.getName();
		EclipsePackage eclipsePackage = packages.get(key);
		if (eclipsePackage == null) {
			eclipsePackage = new EclipsePackage(eclipseXmlPackage.getName(), new ArrayList<EclipseDownloadLink>());
			packages.put(key, eclipsePackage);
			release.getPackages().add(eclipsePackage);
		}

		return eclipsePackage;
	}

	public EclipseDownloads build() {
		return new EclipseDownloads(platforms);
	}
}
