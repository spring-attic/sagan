package sagan.tools.support;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

class EclipseXmlPackage {

    private String name;

    private String icon;

    private String description;

    @JacksonXmlProperty(localName = "download")
    @JacksonXmlElementWrapper(useWrapping=false)
    private List<EclipseXmlDownload> eclipseXmlDownloads;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EclipseXmlDownload> getEclipseXmlDownloads() {
        return eclipseXmlDownloads;
    }

    public void setEclipseXmlDownloads(List<EclipseXmlDownload> eclipseXmlDownloads) {
        this.eclipseXmlDownloads = eclipseXmlDownloads;
    }
}
