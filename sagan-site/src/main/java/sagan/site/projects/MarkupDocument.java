package sagan.site.projects;

import javax.persistence.Embeddable;

/**
 * Document written using a markup language (such as Asciidoctor)
 */
@Embeddable
public class MarkupDocument {

	/**
	 * Source of the document
	 */
	private String source;

	/**
	 * Rendered document in HTML
	 */
	private String html;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
}
