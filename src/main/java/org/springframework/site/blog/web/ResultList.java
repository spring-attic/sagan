package org.springframework.site.blog.web;

import org.springframework.site.blog.PaginationInfo;

import java.util.List;

public class ResultList<T> {

	private final List<T> items;
	private final PaginationInfo paginationInfo;

	public ResultList(List<T> items, PaginationInfo paginationInfo) {
		this.items = items;
		this.paginationInfo = paginationInfo;
	}

	public List<T> getItems() {
		return items;
	}

	public PaginationInfo getPaginationInfo() {
		return paginationInfo;
	}
}
