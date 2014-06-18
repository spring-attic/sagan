package sagan.blog;

import sagan.support.time.DateTimeFactory;
import sagan.support.time.DateTimeUtils;
import sagan.team.MemberProfile;

import java.text.ParseException;
import java.time.LocalDateTime;

public class PostBuilder {

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
    private DateTimeFactory dateTimeFactory;

    public PostBuilder() {
        this(DateTimeFactory.withDefaultTimeZone());
    }

    public PostBuilder(DateTimeFactory dateTimeFactory) {
        title = "My Post";
        author = new MemberProfile();
        author.setUsername("test");
        category = PostCategory.ENGINEERING;
        rawContent = "post body";
        renderedContent = "post body";
        renderedSummary = "summary";
        broadcast = false;
        this.dateTimeFactory = dateTimeFactory;
        publishAt = dateTimeFactory.now();
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
        createdAt = DateTimeUtils.parseDateTimeNoSeconds(dateString);
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
        return publishAt(DateTimeUtils.parseDateTimeNoSeconds(dateString));
    }

    public PostBuilder publishYesterday() {
        return publishAt(dateTimeFactory.now().minusDays(1));
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
