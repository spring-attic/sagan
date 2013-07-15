package org.springframework.site.domain.tools.eclipse.parser;

import org.springframework.site.domain.tools.eclipse.EclipseDownloads;
import org.springframework.site.domain.tools.eclipse.EclipsePackage;
import org.springframework.site.domain.tools.eclipse.EclipsePlatform;
import org.springframework.site.domain.tools.eclipse.xml.Download;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXml;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlPackage;
import org.springframework.site.domain.tools.eclipse.xml.Product;

import java.util.HashMap;
import java.util.Map;

public class EclipseDownloadsXmlConverter {
	public EclipseDownloads convert(EclipseXml xml) {
		Map<String, EclipsePlatform> platforms = new HashMap<String, EclipsePlatform>();

		EclipseDownloads eclipseDownloads = new EclipseDownloads(platforms);

		for (Product product : xml.getProducts()) {
			if (product.getName().equalsIgnoreCase("SpringSource Tool Suites Downloads")) {
				continue;
			}
			for (EclipseXmlPackage eclipseXmlPackage : product.getPackages()) {
				for (Download download : eclipseXmlPackage.getDownloads()) {
					EclipsePlatform platform = getOrCreateEclipsePlatform(platforms, download);
					EclipsePackage eclipsePackage = buildEclipsePackage(eclipseXmlPackage);
					platform.addPackage(eclipsePackage);
				}
			}
		}

		return eclipseDownloads;
	}

	private EclipsePackage buildEclipsePackage(EclipseXmlPackage eclipseXmlPackage) {
		String name = eclipseXmlPackage.getName();
		name = name.replaceAll(" [\\d)].*", "");
		return new EclipsePackage(name);
	}

	private EclipsePlatform getOrCreateEclipsePlatform(Map<String, EclipsePlatform> platforms, Download download) {
		String os = download.getOs();
		if (platforms.containsKey(os)) {
			return platforms.get(os);
		}else{
			EclipsePlatform platform = new EclipsePlatform(os);
			platforms.put(os, platform);
			return platform;
		}
	}
}
