package io.spring.site.web;

public class PageElement {
    private final String pageText;
    private final boolean isNavigable;
	private final boolean isCurrentPage;

	public PageElement(long pageNumber, boolean isNavigable, boolean isCurrentPage) {
        this(pageNumber + "", isNavigable, isCurrentPage);
    }

    public PageElement(String pageText, boolean isNavigable, boolean isCurrentPage) {
        this.pageText = pageText;
        this.isNavigable = isNavigable;
		this.isCurrentPage = isCurrentPage;
	}

    public boolean isNavigable() {
        return isNavigable;
    }

    public String getPageText() {
        return pageText;
    }

	public boolean isCurrentPage() {
		return isCurrentPage;
	}
}
