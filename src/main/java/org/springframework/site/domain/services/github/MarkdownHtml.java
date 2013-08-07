package org.springframework.site.domain.services.github;

public class MarkdownHtml {
	private String html;

	public MarkdownHtml(String html) {
		this.html = html;
	}

	@Override
	public String toString() {
		return html;
	}
}
