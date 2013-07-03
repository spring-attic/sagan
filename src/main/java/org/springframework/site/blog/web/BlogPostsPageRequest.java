package org.springframework.site.blog.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SuppressWarnings("serial")
public class BlogPostsPageRequest extends PageRequest {
	//TODO remove this
	public BlogPostsPageRequest(int page) {
		super(page, 10, Sort.Direction.DESC, "createdAt");
	}

	/**
	 * @param page not zero indexed
	 */
	public static Pageable forLists(int page) {
		return build(page - 1, 10);
	}

	public static Pageable forDashboard() {
		return build(0, Integer.MAX_VALUE);
	}

	public static Pageable forFeeds(){
		return build(0, 20);
	}

	private static Pageable build(int page, int pageSize) {
		return new PageRequest(page, pageSize, Sort.Direction.DESC, "createdAt");
	}

	public static Pageable forSearch(int page) {
		return new PageRequest(page - 1, 10);
	}

}
