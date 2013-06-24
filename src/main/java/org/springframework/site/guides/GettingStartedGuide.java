package org.springframework.site.guides;

public class GettingStartedGuide {
	private final String content;
	private final String sidebar;

	public GettingStartedGuide(String content, String sidebar) {
		this.content = content;
		this.sidebar = sidebar;
	}

	public String getContent() {
		return content;
	}

	public String getSidebar() {
		return sidebar;
	}
}
