package org.springframework.site.blog;

import java.util.Date;

public class PostBuilder {

	private String title;
	private PostCategory category;
	private String rawContent;
	private String renderedContent;
	private String renderedSummary;
	private Date date;
	private boolean broadcast;
	private boolean draft;

	public PostBuilder() {
		this.title = "My Post";
		this.category =	PostCategory.ENGINEERING;
		this.rawContent = "post body";
		this.renderedContent = "post body";
		this.renderedSummary = "summary";
		this.broadcast = false;
		this.draft = false;
	}

	public static PostBuilder post() {
		return new PostBuilder();
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

	public Post build() {
		Post post = new Post(title, rawContent, category);
		post.setRenderedContent(renderedContent);
		post.setRenderedSummary(renderedSummary);
		if (date != null) {
			post.setCreatedDate(date);
		}
		post.setBroadcast(broadcast);
		post.setDraft(draft);
		return post;
	}

	public PostBuilder isBroadcast() {
		broadcast = true;
		return this;
	}
}
