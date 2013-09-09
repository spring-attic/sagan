package io.spring.site.domain.tools.eclipse.parser;

import io.spring.site.domain.tools.eclipse.EclipseDownloads;
import io.spring.site.domain.tools.eclipse.xml.EclipseXml;
import io.spring.site.domain.tools.eclipse.xml.EclipseXmlDownload;
import io.spring.site.domain.tools.eclipse.xml.EclipseXmlPackage;
import io.spring.site.domain.tools.eclipse.xml.EclipseXmlProduct;

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
