package sagan.blog;

import sagan.team.MemberProfile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

import org.springframework.util.StringUtils;

/**
 * JPA Entity representing an individual blog post.
 */
@Entity
@SuppressWarnings("serial")
public class Post implements Serializable {

    private static final DateTimeFormatter SLUG_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

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
    @Type(type = "text")
    private String rawContent;

    @Column(nullable = false)
    @Type(type = "text")
    private String renderedContent;

    @Column(nullable = false)
    @Type(type = "text")
    private String renderedSummary;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean draft = true;

    @Column(nullable = false)
    private boolean broadcast = false;

    @Column(nullable = true)
    private LocalDateTime publishAt;

    @Column(nullable = true)
    private String publicSlug;

    @ElementCollection
    private Set<String> publicSlugAliases = new HashSet<>();

    @SuppressWarnings("unused")
    private Post() {
    }

    public Post(String title, String content, PostCategory category) {
        this.title = title;
        rawContent = content;
        this.category = category;
    }

    /* For testing only */
    public Post(Long id, String title, String content, PostCategory category) {
        this(title, content, category);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(LocalDateTime publishAt) {
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

    public boolean isScheduled() {
        return publishAt == null;
    }

    public boolean isLiveOn(LocalDateTime date) {
        return !(isDraft() || publishAt.isAfter(date));
    }

    public String getPublicSlug() {
        return publicSlug;
    }

    public void addPublicSlugAlias(String alias) {
        if (alias != null) {
            this.publicSlugAliases.add(alias);
        }
    }

    public String getAdminSlug() {
        return String.format("%s-%s", getId(), getSlug());
    }

    private String generatePublicSlug() {
        return String.format("%s/%s", getPublishAt().format(SLUG_DATE_FORMATTER), getSlug());
    }

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
