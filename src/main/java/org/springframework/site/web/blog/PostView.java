package org.springframework.site.web.blog;

import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.domain.team.MemberProfile;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostView {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");

	private Post post;
	private DateService dateService;

	public PostView(Post post, DateService dateService) {
		this.post = post;
		this.dateService = dateService;
	}

	public String getFormattedPublishDate() {
		return post.isScheduled() ? "Unscheduled" : DATE_FORMAT.format(post.getPublishAt());
	}

	public String getPath() {
		String prefix = post.isLiveOn(dateService.now()) ? "/blog/" : "/admin/blog/";
		return prefix + post.getSlug();
	}

	public String getSlug() {
		return post.getSlug();
	}

	public String getTitle() {
		return post.getTitle();
	}

	public boolean isScheduled() {
		return post.isScheduled();
	}

	public boolean isDraft() {
		return post.isDraft();
	}

	public PostCategory getCategory() {
		return post.getCategory();
	}

	public boolean isBroadcast() {
		return post.isBroadcast();
	}

	public MemberProfile getAuthor() {
		return post.getAuthor();
	}

	public String getRenderedSummary() {
		return post.getRenderedSummary();
	}

	public String getRenderedContent() {
		return post.getRenderedContent();
	}

	public Date getPublishAt() {
		return post.getPublishAt();
	}

	public Date getCreatedAt() {
		return post.getCreatedAt();
	}

	public Long getId() {
		return post.getId();
	}
}
