package io.spring.site.web.blog;


import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostCategory;
import io.spring.site.domain.services.DateService;
import io.spring.site.domain.team.MemberProfile;

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
        return this.post.isScheduled() ? "Unscheduled" : DATE_FORMAT.format(this.post
                .getPublishAt());
    }

    public String getPath() {
        String path;
        if (this.post.isLiveOn(this.dateService.now())) {
            path = "/blog/" + post.getPublicSlug();
        } else {
            path = "/admin/blog/" + post.getAdminSlug();
        }
        return path;
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

    public boolean showReadMore() {
        return !this.post.getRenderedContent().equals(this.post.getRenderedSummary());
    }

    public String getEditPath() {
        return getUpdatePath() + "/edit";
    }

    public String getUpdatePath() {
        return "/admin/blog/" + post.getAdminSlug();
    }

    public String getTwitterFeedbackUrl() {
        return "https://twitter.com/intent/tweet?text=@springcentral&url=http://spring.io/blog/" + post.getPublicSlug();
    }

}
