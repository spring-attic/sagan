package org.springframework.site.web.blog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.domain.team.MemberProfile;

public class PostView {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"MMMM dd, yyyy");

	private Post post;
	private DateService dateService;

	public PostView(Post post, DateService dateService) {
		this.post = post;
		this.dateService = dateService;
	}

	public String getFormattedPublishDate() {
		return this.post.isScheduled() ? "Unscheduled" : DATE_FORMAT.format(this.post
				.getPublishAt());
	}

	public String getPath() {
		String prefix = this.post.isLiveOn(this.dateService.now()) ? "/blog/"
				: "/admin/blog/";
		return prefix + this.post.getSlug();
	}

	public String getSlug() {
		return this.post.getSlug();
	}

	public String getTitle() {
		return this.post.getTitle();
	}

	public boolean isScheduled() {
		return this.post.isScheduled();
	}

	public boolean isDraft() {
		return this.post.isDraft();
	}

	public PostCategory getCategory() {
		return this.post.getCategory();
	}

	public boolean isBroadcast() {
		return this.post.isBroadcast();
	}

	public MemberProfile getAuthor() {
		return this.post.getAuthor();
	}

	public String getRenderedSummary() {
		return this.post.getRenderedSummary();
	}

	public String getRenderedContent() {
		return this.post.getRenderedContent();
	}

	public Date getPublishAt() {
		return this.post.getPublishAt();
	}

	public Date getCreatedAt() {
		return this.post.getCreatedAt();
	}

	public Long getId() {
		return this.post.getId();
	}
}
