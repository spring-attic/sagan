package io.spring.site.domain.understanding;

public class UnderstandingGuide {
	private final String subject;
	private String content;
	private String sidebar;

	public UnderstandingGuide(String subject, String content, String sidebar) {
		this.subject = subject;
		this.content = content;
		this.sidebar = sidebar;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

	public String getSidebar() {
		return sidebar;
	}

}
