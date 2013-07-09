package org.springframework.search;

import java.util.Date;

public class SearchEntry {

	//TODO what's the ID strategy for search results?
	private String id;

	private String path;

	private String summary;

	private String rawContent;

	private String title;

	// TODO: maybe we don't need this in the index?
	private Date publishAt = new Date();

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getPublishAt() {
		return publishAt;
	}

	public void setPublishAt(Date publishAt) {
		this.publishAt = publishAt;
	}

	public String getRawContent() {
		return rawContent;
	}

	public void setRawContent(String rawContent) {
		this.rawContent = rawContent;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
