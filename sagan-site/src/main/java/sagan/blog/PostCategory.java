package sagan.blog;

/**
 * Available categories for blog posts.
 */
public enum PostCategory {

    ENGINEERING("Engineering", "engineering"),
    RELEASES("Releases", "releases"),
    NEWS_AND_EVENTS("News and Events", "news");

    private String displayName;
    private String urlSlug;

    PostCategory(String displayName, String urlSlug) {
        this.displayName = displayName;
        this.urlSlug = urlSlug;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUrlSlug() {
        return urlSlug;
    }

    public String getId() {
        return name();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
