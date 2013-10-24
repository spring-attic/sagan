package sagan.tools.eclipse.xml;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class EclipseXml {
    @ElementList(entry = "product", type = EclipseXmlProduct.class, inline = true)
    private List<EclipseXmlProduct> eclipseXmlProducts;

    public List<EclipseXmlProduct> getEclipseXmlProducts() {
        return eclipseXmlProducts;
    }

    public void setEclipseXmlProducts(List<EclipseXmlProduct> eclipseXmlProducts) {
        this.eclipseXmlProducts = eclipseXmlProducts;
    }
}
