package sagan.blog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;
import sagan.team.MemberProfile;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA Entity representing an individual blog post.
 */
@Entity
@SuppressWarnings("serial")
public class Post {

    private static final SimpleDateFormat SLUG_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    private MemberProfile author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostFormat format;

    @Column(nullable = false)
    @Type(type = "text")
    private String rawContent;

    @Column(nullable = false)
    @Type(type = "text")
    private String renderedContent;

    @Column(nullable = false)
    @Type(type = "text")
    private String renderedSummary;

    @Column(nullable = false)
    private Date createdAt = new Date();

    @Column(nullable = false)
    private boolean draft = true;

    @Column(nullable = false)
    private boolean broadcast = false;

    @Column(nullable = true)
    private Date publishAt;

    @Column(nullable = true)
    private String publicSlug;

    @ElementCollection
    private Set<String> publicSlugAliases = new HashSet<>();

    @SuppressWarnings("unused")
    private Post() {
    }

    public Post(String title, String content, PostCategory category, PostFormat format) {
        this.title = title;
        this.rawContent = content;
        this.category = category;
        this.format = format;
    }

    /* For testing only */
    public Post(Long id, String title, String content, PostCategory category, PostFormat format) {
        this(title, content, category, format);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public MemberProfile getAuthor() {
        return author;
    }

    public void setAuthor(MemberProfile author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PostCategory getCategory() {
        return category;
    }

    public void setCategory(PostCategory category) {
        this.category = category;
    }

    public PostFormat getFormat() {
        return format;
    }

    public void setFormat(PostFormat format) {
        this.format = format;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getRenderedContent() {
        return renderedContent;
    }

    public void setRenderedContent(String renderedContent) {
        this.renderedContent = renderedContent;
    }

    public String getRenderedSummary() {
        return renderedSummary;
    }

    public void setRenderedSummary(String renderedSummary) {
        this.renderedSummary = renderedSummary;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(Date publishAt) {
        this.publishAt = publishAt;
        publicSlug = publishAt == null ? null : generatePublicSlug();
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public void setBroadcast(boolean isBroadcast) {
        broadcast = isBroadcast;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    @JsonIgnore
    public boolean isScheduled() {
        return publishAt == null;
    }

    @JsonIgnore
    public boolean isLiveOn(Date date) {
        return !(isDraft() || publishAt.after(date));
    }

    public String getPublicSlug() {
        return publicSlug;
    }

    public void addPublicSlugAlias(String alias) {
        if (alias != null) {
            this.publicSlugAliases.add(alias);
        }
    }

    @JsonIgnore
    public String getAdminSlug() {
        return String.format("%s-%s", getId(), getSlug());
    }

    private String generatePublicSlug() {
        return String.format("%s/%s", SLUG_DATE_FORMAT.format(getPublishAt()), getSlug());
    }

    @JsonIgnore
    private String getSlug() {
        if (title == null) {
            return "";
        }

        String cleanedTitle = title.toLowerCase().replace("\n", " ").replaceAll("[^a-z\\d\\s]", " ");
        return StringUtils.arrayToDelimitedString(StringUtils.tokenizeToStringArray(cleanedTitle, " "), "-");
    }

    @Override
    public String toString() {
        return "Post{" + "id=" + id + ", title='" + title + '\'' + '}';
    }
}
