package io.spring.site.web.blog;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class BlogPostsNotFound extends RuntimeException {
    public BlogPostsNotFound(String message) {
        super(message);
    }
}
