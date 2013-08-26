package io.spring.site.domain.tools.eclipse.xml;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict=false)
public class EclipseXml {
	@ElementList(entry="product", type = EclipseXmlProduct.class,  inline = true)
	private List<EclipseXmlProduct> eclipseXmlProducts;

	public List<EclipseXmlProduct> getEclipseXmlProducts() {
		return eclipseXmlProducts;
	}

	public void setEclipseXmlProducts(List<EclipseXmlProduct> eclipseXmlProducts) {
		this.eclipseXmlProducts = eclipseXmlProducts;
	}
}
