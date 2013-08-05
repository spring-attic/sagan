package org.springframework.site.search;

public class SearchResult {
	private final String type;
	private String title;
	private String summary;
	private String path;
	private String id;
	private String highlight;
	private final String originalSearchTerm;

	public SearchResult(String id, String title, String summary, String path, String type, String highlight, String originalSearchTerm) {
		this.title = title;
		this.summary = summary;
		this.path = path;
		this.id = id;
		this.type = type;
		this.highlight = highlight;
		this.originalSearchTerm = originalSearchTerm;
	}

	public String getTitle() {
		return title;
	}

	public String getSummary() {
		return summary;
	}

	public String getPath() {
		return path;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getHighlight() {
		return highlight;
	}

	public String getOriginalSearchTerm() {
		return originalSearchTerm;
	}

	public String getDisplayText() {
		if (getType().equals("apiDoc") && getTitle().startsWith(getOriginalSearchTerm())) {
			return getSummary();
		} else {
			return getHighlight() != null ? getHighlight() : getSummary();
		}
	}
}
