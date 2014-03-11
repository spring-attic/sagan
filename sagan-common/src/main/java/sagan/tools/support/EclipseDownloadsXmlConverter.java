package sagan.tools.support;

import sagan.tools.EclipseDownloads;

class EclipseDownloadsXmlConverter {
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
