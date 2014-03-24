package sagan.blog;

import sagan.support.ResourceNotFoundException;

@SuppressWarnings("serial")
public class PostNotFoundException extends ResourceNotFoundException {

    public PostNotFoundException(long id) {
        super("Could not find blog post with id " + id);
    }

    public PostNotFoundException(String slug) {
        super("Could not find blog post with slug '" + slug + "'");
    }
}
