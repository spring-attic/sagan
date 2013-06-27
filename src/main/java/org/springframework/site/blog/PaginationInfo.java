package org.springframework.site.blog;

import org.springframework.data.domain.Page;

/**
 * Used in views for rendering pagination controls
 * Page numbers are not zero indexed
 */
public class PaginationInfo {
	private final long currentPage;
	private final long totalPages;

	public PaginationInfo(Page page) {
		currentPage = page.getNumber() + 1;
		totalPages = page.getTotalPages();
	}

	public boolean isVisible() {
		return isPreviousVisible() || isNextVisible();
	}

	public boolean isPreviousVisible() {
		return currentPage > 1;
	}

	public boolean isNextVisible() {
		return currentPage < totalPages;
	}

	public long getNextPageNumber() {
		return currentPage + 1;
	}

	public long getPreviousPageNumber() {
		return currentPage - 1;
	}

	public long getCurrentPage() {
		return currentPage;
	}

	public long getTotalPages() {
		return totalPages;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PaginationInfo that = (PaginationInfo) o;

		if (currentPage != that.currentPage) return false;
		if (totalPages != that.totalPages) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (currentPage ^ (currentPage >>> 32));
		result = 31 * result + (int) (totalPages ^ (totalPages >>> 32));
		return result;
	}
}
