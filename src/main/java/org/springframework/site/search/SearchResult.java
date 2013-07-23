package org.springframework.site.search;

public class SearchResult {
	private String title;
	private String summary;
	private String path;
	private String id;

	public SearchResult(String id, String title, String summary, String path) {
		this.title = title;
		this.summary = summary;
		this.path = path;
		this.id = id;
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
}
