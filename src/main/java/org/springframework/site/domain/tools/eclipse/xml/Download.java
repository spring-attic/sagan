package org.springframework.site.domain.tools.eclipse.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class Download {

	@Attribute
	private String os;

	@Attribute(name="version", required = false)
	private String version;

	@Attribute(name="eclipse-version")
	private String eclipseVersion;

	@Attribute
	private String size;

	@Element(name="description")
	private String description;
	@Element
	private String file;
	@Element
	private String md5;
	@Element
	private String sha1;
	@Element
	private String bucket;

	public void setSize(String size) {
		this.size = size;
	}

	public String getFile() {
		return file;
	}

}
