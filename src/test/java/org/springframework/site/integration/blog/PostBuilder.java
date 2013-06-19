package org.springframework.site.integration.blog;

import org.springframework.site.blog.Post;

import java.util.Date;

public class PostBuilder {

	private String title;
	private String rawContent;
	private String renderedContent;
	private Date date;

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

	public PostBuilder dateCreated(Date date) {
		this.date = date;
		return this;
	}

	public Post build() {
		Post post = new Post(title, rawContent);
		post.setRenderedContent(renderedContent);
		if (date != null) {
			post.setCreatedDate(date);
		}
		return post;
	}
}
