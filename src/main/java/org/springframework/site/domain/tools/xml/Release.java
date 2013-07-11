package org.springframework.site.domain.tools.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.List;

public class Release {

	@Attribute
	private String name;

	@ElementList(name = "download", type = Download.class, inline = true)
	private List<Download> downloads;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Download> getDownloads() {
		return downloads;
	}

	public void setDownloads(List<Download> downloads) {
		this.downloads = downloads;
	}
}
