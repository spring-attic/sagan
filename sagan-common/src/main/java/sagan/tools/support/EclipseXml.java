package sagan.tools.support;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

class EclipseXml {
    @JacksonXmlProperty(localName = "product")
    @JacksonXmlElementWrapper(useWrapping=false)
    private List<EclipseXmlProduct> eclipseXmlProducts;

    public List<EclipseXmlProduct> getEclipseXmlProducts() {
        return eclipseXmlProducts;
    }

    public void setEclipseXmlProducts(List<EclipseXmlProduct> eclipseXmlProducts) {
        this.eclipseXmlProducts = eclipseXmlProducts;
    }
}
