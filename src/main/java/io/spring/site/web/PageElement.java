package io.spring.site.web;

public class PageElement {
	private final String pageText;
	private final boolean isActive;

	public PageElement(long pageNumber, boolean isActive) {
		this(pageNumber + "", isActive);
	}

	public PageElement(String pageText, boolean isActive) {
		this.pageText = pageText;
		this.isActive = isActive;
	}

	public boolean isActive() {
		return isActive;
	}

	public String getPageText() {
		return pageText;
	}
}
