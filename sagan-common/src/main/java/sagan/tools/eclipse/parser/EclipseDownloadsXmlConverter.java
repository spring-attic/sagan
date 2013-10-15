package sagan.tools.eclipse.parser;

import sagan.tools.eclipse.EclipseDownloads;
import sagan.tools.eclipse.xml.EclipseXml;
import sagan.tools.eclipse.xml.EclipseXmlDownload;
import sagan.tools.eclipse.xml.EclipseXmlPackage;
import sagan.tools.eclipse.xml.EclipseXmlProduct;

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
