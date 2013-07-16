package org.springframework.site.domain.tools.eclipse;

import java.util.List;

public class EclipsePackage {
	private String name;
	private List<EclipseDownloadLink> downloads;

	public EclipsePackage(String name, List<EclipseDownloadLink> downloadLinks) {
		this.name = name;
		this.downloads = downloadLinks;
	}

	public String getName() {
		return name;
	}

	public List<EclipseDownloadLink> getDownloadLinks() {
		return downloads;
	}
}
