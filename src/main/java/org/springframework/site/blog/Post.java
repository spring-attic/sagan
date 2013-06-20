package org.springframework.site.blog;

import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Post implements Serializable {

	private static final long serialVersionUID = 6541602744396162438L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String title;

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

	public Post() {
	}

	public Post(String title, String content) {
		this.title = title;
		this.rawContent = content;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
}
