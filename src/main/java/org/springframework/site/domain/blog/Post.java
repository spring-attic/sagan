package org.springframework.site.domain.blog;

import org.hibernate.annotations.Type;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Post implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private MemberProfile author;

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
	private Date createdAt = new Date();

	@Column(nullable = false)
	private boolean draft = true;

	@Column(nullable = false)
	private boolean broadcast = false;

	@Column(nullable = true)
	private Date publishAt;

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

	public MemberProfile getAuthor() {
		return author;
	}

	public void setAuthor(MemberProfile author) {
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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getPublishAt() {
		return publishAt;
	}

	public void setPublishAt(Date publishAt) {
		this.publishAt = publishAt;
	}

	public String getSlug() {
		if (title == null) {
			return "";
		}

		String cleanedTitle = title.toLowerCase().replace("\n", " ").replaceAll("[^a-z\\d\\s]", " ");
		return getId() + "-" + StringUtils.arrayToDelimitedString(StringUtils.tokenizeToStringArray(cleanedTitle, " "), "-");
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

	public boolean isScheduled() {
		return publishAt == null;
	}

	public boolean isLiveOn(Date date) {
		return !(isDraft() || publishAt.after(date));
	}

	@Override
	public String toString() {
		return "Post{" +
				"id=" + id +
				", title='" + title + '\'' +
				'}';
	}
}
