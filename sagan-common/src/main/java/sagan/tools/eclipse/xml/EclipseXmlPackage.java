package sagan.tools.eclipse.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

public class EclipseXmlPackage {

    @Attribute
    private String name;

    @Attribute
    private String icon;

    @Element( data = true)
    private String description;

    @ElementList(entry = "download", type = EclipseXmlDownload.class, inline = true)
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
