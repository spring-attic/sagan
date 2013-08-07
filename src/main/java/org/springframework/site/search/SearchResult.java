package org.springframework.site.search;

public class SearchResult {
	private final String type;
	private final String title;
	private final String summary;
	private final String path;
	private final String id;
	private final String highlight;
	private final String originalSearchTerm;
	private final String projectId;
	private final String version;

	public SearchResult(String id, String title, String summary, String path, String type, String highlight, String originalSearchTerm, String projectId, String version) {
		this.title = title;
		this.summary = summary;
		this.path = path;
		this.id = id;
		this.type = type;
		this.highlight = highlight;
		this.originalSearchTerm = originalSearchTerm;
		this.projectId = projectId;
		this.version = version;
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

	public String getProjectId() {
		return projectId;
	}

	public String getVersion() {
		return version;
	}

	public String getDisplayText() {
		if (getType().equals("apiDoc") && getTitle().startsWith(getOriginalSearchTerm())) {
			return getSummary();
		} else {
			return getHighlight() != null ? getHighlight() : getSummary();
		}
	}
}
