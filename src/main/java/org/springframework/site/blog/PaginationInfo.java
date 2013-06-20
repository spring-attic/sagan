package org.springframework.site.blog;

public class PaginationInfo {
	private final long currentPage;
	private final long totalPages;

	/**
	 * Create a new PaginationInfo, which is used in views for rendering pagination controls
	 * @param currentPage the current page, starting at 1
	 * @param totalPages the total number of pages
	 */
	public PaginationInfo(int currentPage, long totalPages) {
		this.currentPage = currentPage;
		this.totalPages = totalPages;
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
