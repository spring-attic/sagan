package sagan.blog;

@SuppressWarnings("serial")
public class BlogPostMovedException extends RuntimeException {
    private String publicSlug;

    public BlogPostMovedException(String publicSlug) {
        this.publicSlug = publicSlug;
    }

    public String getPublicSlug() {
        return publicSlug;
    }
}