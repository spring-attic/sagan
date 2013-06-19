package org.springframework.site.integration.blog;

import org.springframework.site.blog.Post;

public class PostBuilder {

	private String title;
	private String rawContent;
	private String renderedContent;

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

	public Post build() {
		Post post = new Post(title, rawContent);
		post.setRenderedContent(renderedContent);
		return post;
	}
}
