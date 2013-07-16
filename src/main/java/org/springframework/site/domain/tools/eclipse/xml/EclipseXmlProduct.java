package org.springframework.site.domain.tools.eclipse.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.List;

public class EclipseXmlProduct {
	@Attribute
	private String name;

	@ElementList(entry = "package", type = EclipseXmlPackage.class, inline = true)
	private List<EclipseXmlPackage> packages;

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
