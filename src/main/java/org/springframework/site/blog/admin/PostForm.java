package org.springframework.site.blog.admin;

import org.springframework.site.blog.PostCategory;

public class PostForm {
	private String title;
	private String content;
	private PostCategory category;
	private boolean isBroadcast;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public PostCategory getCategory() {
		return category;
	}

	public void setCategory(PostCategory category) {
		this.category = category;
	}

	public boolean isBroadcast() {
		return isBroadcast;
	}

	public void setBroadcast(boolean broadcast) {
		isBroadcast = broadcast;
	}
}
