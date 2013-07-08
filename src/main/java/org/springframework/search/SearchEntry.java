package org.springframework.search;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Document(indexName = "site", type = "site")
@Entity
@SuppressWarnings("unused")
public class SearchEntry {

	//TODO what's the ID strategy for search results?
	@Id
	private String id;

	@Column
	private String path;

	@Column
	private String summary;

	@Column
	private String rawContent;

	@Column
	private String title;

	// TODO: maybe we don't need this in the index?
	@Column
	@Field(type = FieldType.Long)
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
