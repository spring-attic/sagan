package org.springframework.site.domain.tools.eclipse.xml;

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

	@ElementList(entry = "download", type = Download.class, inline = true)
	private List<Download> downloads;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}


	public List<Download> getDownloads() {
		return downloads;
	}

}
