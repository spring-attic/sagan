package org.springframework.site.guides;

public class GettingStartedGuide {
	private final String content;
	private final String sidebar;
	private final String zipUrl;

	public GettingStartedGuide(String content, String sidebar, String zipUrl) {
		this.content = content;
		this.sidebar = sidebar;
		this.zipUrl = zipUrl;
	}

	public String getContent() {
		return content;
	}

	public String getSidebar() {
		return sidebar;
	}

	public String getZipUrl() {
		return zipUrl;
	}
}
