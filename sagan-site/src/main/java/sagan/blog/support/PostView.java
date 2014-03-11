package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.team.MemberProfile;
import sagan.support.DateService;

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
        String path;
        if (post.isLiveOn(dateService.now())) {
            path = "/blog/" + post.getPublicSlug();
        } else {
            path = "/admin/blog/" + post.getAdminSlug();
        }
        return path;
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

    public boolean showReadMore() {
        return !post.getRenderedContent().equals(post.getRenderedSummary());
    }

    public String getEditPath() {
        return getUpdatePath() + "/edit";
    }

    public String getUpdatePath() {
        return "/admin/blog/" + post.getAdminSlug();
    }
}
