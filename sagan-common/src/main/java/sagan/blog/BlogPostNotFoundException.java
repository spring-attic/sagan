package sagan.blog;

import sagan.util.ResourceNotFoundException;

@SuppressWarnings("serial")
public class BlogPostNotFoundException extends ResourceNotFoundException {

    public BlogPostNotFoundException(long id) {
        super("Blog post not found with id " + id);
    }

    public BlogPostNotFoundException(String slug) {
        super("Blog post not found with slug '" + slug + "'");
    }
}
