package sagan.site.blog;

import sagan.site.support.ResourceNotFoundException;

/**
 * Exception thrown when requesting a non existent (or no-longer existent) blog post. See
 * sagan-site's {@code MvcConfig#handleException} for handling logic.
 *
 * @see sagan.site.blog.support.BlogService#getPost(Long)
 * @see sagan.site.blog.support.BlogService#getPublishedPost(String)
 */
@SuppressWarnings("serial")
public class PostNotFoundException extends ResourceNotFoundException {

    public PostNotFoundException(long id) {
        super("Could not find blog post with id " + id);
    }

    public PostNotFoundException(String slug) {
        super("Could not find blog post with slug '" + slug + "'");
    }
}
