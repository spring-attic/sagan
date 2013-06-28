package org.springframework.site.blog.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SuppressWarnings("serial")
public class BlogPostsPageRequest extends PageRequest {
	public BlogPostsPageRequest(int page) {
		super(page, 10, Sort.Direction.DESC, "createdAt");
	}

	/**
	 * @param page not zero indexed
	 */
	public static Pageable forLists(int page) {
		return new PageRequest(page - 1, 10, Sort.Direction.DESC, "createdAt");
	}

	public static Pageable forFeeds(){
		return new PageRequest(0, 20, Sort.Direction.DESC, "createdAt");
	}

}
