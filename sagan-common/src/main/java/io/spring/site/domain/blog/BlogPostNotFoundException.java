package io.spring.site.domain.blog;

@SuppressWarnings("serial")
public class BlogPostNotFoundException extends RuntimeException {

    public BlogPostNotFoundException(long id) {
        super("Blog post not found with id " + id);
    }

    public BlogPostNotFoundException(String slug) {
        super("Blog post not found with slug '" + slug + "'");
    }
}
