package org.springframework.site.domain.tools.eclipse.parser;

import org.springframework.site.domain.tools.eclipse.EclipseDownloads;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXml;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlDownload;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlPackage;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlProduct;

public class EclipseDownloadsXmlConverter {
	public EclipseDownloads convert(EclipseXml xml) {
		EclipseDownloadsBuilder builder = new EclipseDownloadsBuilder();

		for (EclipseXmlProduct eclipseXmlProduct : xml.getEclipseXmlProducts()) {
			if (eclipseXmlProduct.getName().equalsIgnoreCase("SpringSource Tool Suites Downloads")) {
				continue;
			}

			for (EclipseXmlPackage eclipseXmlPackage : eclipseXmlProduct.getPackages()) {
				for (EclipseXmlDownload eclipseXmlDownload : eclipseXmlPackage.getEclipseXmlDownloads()) {
					builder.addDownload(eclipseXmlDownload, eclipseXmlPackage, eclipseXmlProduct);
				}
			}
		}

		return builder.build();
	}
}
