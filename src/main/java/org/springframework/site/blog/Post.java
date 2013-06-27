package org.springframework.site.blog;

import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Post implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String author;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PostCategory category;

	@Column(nullable = false)
	@Type(type="text")
	private String rawContent;

	@Column(nullable = false)
	@Type(type="text")
	private String renderedContent;

	@Column(nullable = false)
	@Type(type="text")
	private String renderedSummary;

	@Column(nullable = false)
	private Date createdDate = new Date();

	@Column(nullable = false)
	private boolean draft = true;

	@Column(nullable = false)
	private boolean broadcast = false;

	@SuppressWarnings("unused")
	private Post() {
	}

	public Post(String title, String content, PostCategory category) {
		this.title = title;
		this.rawContent = content;
		this.category = category;
	}

	public Long getId() {
		return id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public PostCategory getCategory() {
		return category;
	}

	public void setCategory(PostCategory category) {
		this.category = category;
	}

	public String getRawContent() {
		return rawContent;
	}

	public void setRawContent(String rawContent) {
		this.rawContent = rawContent;
	}

	public String getRenderedContent() {
		return renderedContent;
	}

	public void setRenderedContent(String renderedContent) {
		this.renderedContent = renderedContent;
	}

	public String getRenderedSummary() {
		return renderedSummary;
	}

	public void setRenderedSummary(String renderedSummary) {
		this.renderedSummary = renderedSummary;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getSlug() {
		if (title == null) {
			return "";
		}

		String cleanedTitle = title.toLowerCase().replace("\n", " ").replaceAll("[^a-z\\d\\s]", "");
		return StringUtils.arrayToDelimitedString(StringUtils.tokenizeToStringArray(cleanedTitle, " "), "-");
	}

	public boolean isDraft() {
		return draft;
	}

	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	public void setBroadcast(boolean isBroadcast) {
		this.broadcast = isBroadcast;
	}

	public boolean isBroadcast() {
		return broadcast;
	}

	public String getPath() {
		return "/blog/" + getId() + "-" + getSlug();
	}


}
