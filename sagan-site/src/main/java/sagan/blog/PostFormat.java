package sagan.blog;

/**
 * Available formats for blog posts.
 */
public enum PostFormat {

    MARKDOWN("Markdown", "markdown"), ASCIIDOC("Asciidoc", "asciidoc");

    private String displayName;
    private String slug;

    PostFormat(String displayName, String slug) {
        this.displayName = displayName;
        this.slug = slug;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSlug() {
        return slug;
    }

    public String getId() {
        return name();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
