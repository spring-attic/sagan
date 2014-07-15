package sagan.tools.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

class EclipseXmlProduct {
    private String name;

    @JacksonXmlProperty(localName = "package")
    @JacksonXmlElementWrapper(useWrapping=false)
    private List<EclipseXmlPackage> packages;

    @JsonIgnore
    private String icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EclipseXmlPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<EclipseXmlPackage> packages) {
        this.packages = packages;
    }

}
