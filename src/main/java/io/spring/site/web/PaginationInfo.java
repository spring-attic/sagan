package io.spring.site.web;

import org.springframework.data.domain.Page;

/**
 * Used in views for rendering pagination controls Page numbers are not zero indexed
 */
public class PaginationInfo {
	private final long currentPage;
	private final long totalPages;

	public PaginationInfo(Page<?> page) {
		this.currentPage = page.getNumber() + 1;
		this.totalPages = page.getTotalPages();
	}

	public boolean isVisible() {
		return isPreviousVisible() || isNextVisible();
	}

	public boolean isPreviousVisible() {
		return this.currentPage > 1;
	}

	public boolean isNextVisible() {
		return this.currentPage < this.totalPages;
	}

	public long getNextPageNumber() {
		return this.currentPage + 1;
	}

	public long getPreviousPageNumber() {
		return this.currentPage - 1;
	}

	public long getCurrentPage() {
		return this.currentPage;
	}

	public long getTotalPages() {
		return this.totalPages;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PaginationInfo that = (PaginationInfo) o;

		if (this.currentPage != that.currentPage)
			return false;
		if (this.totalPages != that.totalPages)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (this.currentPage ^ (this.currentPage >>> 32));
		result = 31 * result + (int) (this.totalPages ^ (this.totalPages >>> 32));
		return result;
	}
}
