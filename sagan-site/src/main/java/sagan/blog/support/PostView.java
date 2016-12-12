package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;
import sagan.team.MemberProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * Value object designed to represent a blog {@link Post} at the view layer, including
 * /blog, /admin/blog, individual /team member pages and blog Atom feeds. Wraps and
 * delegates to an underlying Post for most operations, but introduces methods such as
 * {@link #getPath()} and {@link #getUpdatePath()} with awareness of specific Sagan URL
 * structures not otherwise suitable for inclusion in the Post class.
 */
public final class PostView {

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");

    private final Post post;
    private final DateFactory dateFactory;

    private PostView(Post post, DateFactory dateFactory) {
        this.post = post;
        this.dateFactory = dateFactory;
    }

    public static PostView of(Post post, DateFactory dateFactory) {
        return new PostView(post, dateFactory);
    }

    public static Page<PostView> pageOf(Page<Post> posts, DateFactory dateFactory) {
        List<PostView> postViews = posts.getContent().stream()
                .map(post -> of(post, dateFactory))
                .collect(Collectors.toList());
        PageRequest pageRequest = new PageRequest(posts.getNumber(), posts.getSize(), posts.getSort());
        return new PageImpl<>(postViews, pageRequest, posts.getTotalElements());
    }

    public String getFormattedPublishDate() {
        return post.isScheduled() ? "Unscheduled" : DATE_FORMAT.format(post.getPublishAt());
    }

    public String getPath() {
        String path;
        if (post.isLiveOn(dateFactory.now())) {
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
