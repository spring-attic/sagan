package org.springframework.site.blog.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SuppressWarnings("serial")
public class BlogPostsPageRequest extends PageRequest {
	public BlogPostsPageRequest(int page) {
		super(page, 10, Sort.Direction.DESC, "createdDate");
	}
}
