package org.springframework.site.blog;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class NoSuchBlogPostException extends RuntimeException {

	public NoSuchBlogPostException(String message) {
		super(message);
	}

	public NoSuchBlogPostException(String message, Throwable cause) {
		super(message, cause);
	}
}
