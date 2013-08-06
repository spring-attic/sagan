package org.springframework.site.domain.understanding;

public class UnderstandingGuide {
	private String content;
	private String sidebar;

	public UnderstandingGuide(String content, String sidebar) {
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
