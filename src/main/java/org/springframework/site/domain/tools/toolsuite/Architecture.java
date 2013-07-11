package org.springframework.site.domain.tools.toolsuite;

import java.util.List;

public class Architecture {
	private String name;
	private List<DownloadLink> downloadLinks;

	public Architecture(String name, List<DownloadLink> downloadLinks) {
		this.name = name;
		this.downloadLinks = downloadLinks;
	}

	public String getName() {
		return name;
	}

	public List<DownloadLink> getDownloadLinks() {
		return downloadLinks;
	}
}
