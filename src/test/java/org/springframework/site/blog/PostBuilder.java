package org.springframework.site.blog;

import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostBuilder {

	private Long id;
	private String title;
	private String author;
	private PostCategory category;
	private String rawContent;
	private String renderedContent;
	private String renderedSummary;
	private Date date;
	private Date publishAt;
	private boolean broadcast;
	private boolean draft;

	public PostBuilder() {
		this.title = "My Post";
		this.author = "test";
		this.category =	PostCategory.ENGINEERING;
		this.rawContent = "post body";
		this.renderedContent = "post body";
		this.renderedSummary = "summary";
		this.broadcast = false;
		this.publishAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		this.draft = false;
	}

	public static PostBuilder post() {
		return new PostBuilder();
	}

	public PostBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public PostBuilder author(String author) {
		this.author = author;
		return this;
	}

	public PostBuilder title(String title) {
		this.title = title;
		return this;
	}

	public PostBuilder rawContent(String rawContent) {
		this.rawContent = rawContent;
		return this;
	}

	public PostBuilder renderedContent(String renderedContent) {
		this.renderedContent = renderedContent;
		return this;
	}

	public PostBuilder renderedSummary(String renderedSummary) {
		this.renderedSummary = renderedSummary;
		return this;
	}

	public PostBuilder dateCreated(Date date) {
		this.date = date;
		return this;
	}

	public PostBuilder category(PostCategory category) {
		this.category = category;
		return this;
	}

	public PostBuilder draft() {
		this.draft = true;
		return this;
	}

	public PostBuilder unscheduled() {
		this.publishAt = null;
		return this;
	}

	public PostBuilder publishAt(Date date) {
		this.publishAt = date;
		return this;
	}

	public PostBuilder publishAt(String dateString) throws ParseException {
		this.publishAt = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
		return this;
	}

	public Post build() {
		Post post = new Post(title, rawContent, category);
		post.setAuthor(author);
		post.setRenderedContent(renderedContent);
		post.setRenderedSummary(renderedSummary);
		if (date != null) {
			post.setCreatedAt(date);
		}
		post.setBroadcast(broadcast);
		post.setDraft(draft);
		post.setPublishAt(publishAt);

		ReflectionTestUtils.setField(post, "id", this.id);

		return post;
	}

	public PostBuilder isBroadcast() {
		broadcast = true;
		return this;
	}
}
