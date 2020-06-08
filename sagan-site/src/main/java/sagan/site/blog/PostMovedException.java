package sagan.site.blog;

/**
 * Exception thrown when requesting a blog post whose slug has changed. See
 * {@code BlogController} for handling logic.
 *
 * @see sagan.site.blog.support.BlogService#getPublishedPost(String)
 */
@SuppressWarnings("serial")
public class PostMovedException extends RuntimeException {

    private String publicSlug;

    public PostMovedException(String publicSlug) {
        this.publicSlug = publicSlug;
    }

    public String getPublicSlug() {
        return publicSlug;
    }
}
