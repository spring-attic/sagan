package org.springframework.site.search;

import org.jsoup.nodes.Document;

public class WebDocument {

	private String path;

	private Document document;

	public WebDocument(String path, Document document) {
		this.path = path;
		this.document = document;
	}

	public String getPath() {
		return path;
	}

	public Document getDocument() {
		return document;
	}
}
