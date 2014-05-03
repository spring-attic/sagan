package sagan.blog;

import sagan.team.MemberProfile;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostBuilder {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Long id;
    private String title;
    private MemberProfile author;
    private PostCategory category;
    private String rawContent;
    private String renderedContent;
    private String renderedSummary;
    private LocalDateTime createdAt;
    private LocalDateTime publishAt;
    private boolean broadcast;
    private boolean draft;

    public PostBuilder() {
        title = "My Post";
        author = new MemberProfile();
        author.setUsername("test");
        category = PostCategory.ENGINEERING;
        rawContent = "post body";
        renderedContent = "post body";
        renderedSummary = "summary";
        broadcast = false;
        publishAt = LocalDateTime.now();
        draft = false;
    }

    public static PostBuilder post() {
        return new PostBuilder();
    }

    public PostBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public PostBuilder author(MemberProfile author) {
        this.author = author;
        return this;
    }

    public PostBuilder author(String username, String name) {
        author.setUsername(username);
        author.setName(name);
        return this;
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

    public PostBuilder createdAt(LocalDateTime date) {
        createdAt = date;
        return this;
    }

    public PostBuilder createdAt(String dateString) throws ParseException {
        createdAt = LocalDateTime.parse(dateString, DATE_TIME_FORMATTER);
        return this;
    }

    public PostBuilder category(PostCategory category) {
        this.category = category;
        return this;
    }

    public PostBuilder draft() {
        draft = true;
        return this;
    }

    public PostBuilder unscheduled() {
        publishAt = null;
        return this;
    }

    public PostBuilder publishAt(LocalDateTime date) {
        publishAt = date;
        return this;
    }

    public PostBuilder publishAt(String dateString) throws ParseException {
        return publishAt(LocalDateTime.parse(dateString, DATE_TIME_FORMATTER));
    }

    public PostBuilder publishYesterday() {
        return publishAt(LocalDateTime.now().minusDays(1));
    }

    public PostBuilder isBroadcast() {
        broadcast = true;
        return this;
    }

    public Post build() {
        Post post = new Post(id, title, rawContent, category);
        post.setAuthor(author);
        post.setRenderedContent(renderedContent);
        post.setRenderedSummary(renderedSummary);
        if (createdAt != null) {
            post.setCreatedAt(createdAt);
        }
        post.setBroadcast(broadcast);
        post.setDraft(draft);
        post.setPublishAt(publishAt);

        return post;
    }

}
