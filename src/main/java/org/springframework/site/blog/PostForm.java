package org.springframework.site.blog;

public class PostForm {
	private String title;
	private String content;
	private PostCategory category;
	private boolean broadcast;
    private boolean draft;

	public PostForm() {	}

	public PostForm(Post post) {
		title = post.getTitle();
		content = post.getRawContent();
		category = post.getCategory();
		broadcast = post.isBroadcast();
		draft = post.isDraft();
	}

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
		return broadcast;
	}

	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

}
