package org.springframework.site.domain.tools.eclipse.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.List;

public class Product {
	@Attribute
	private String name;

	@ElementList(entry = "package", type = EclipsePackage.class, inline = true)
	private List<EclipsePackage> packages;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EclipsePackage> getPackages() {
		return packages;
	}

	public void setPackages(List<EclipsePackage> packages) {
		this.packages = packages;
	}
}
