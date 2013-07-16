package org.springframework.site.domain.tools.eclipse;

public class EclipseDownloadLink {
	private final String name;
	private String url;

	public EclipseDownloadLink(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
