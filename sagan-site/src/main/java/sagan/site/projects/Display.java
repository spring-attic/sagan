package sagan.site.projects;

import javax.persistence.Embeddable;

/**
 * Project information useful for displaying the project on the website
 */
@Embeddable
public class Display {

	private String siteUrl;

	private boolean featured;

	private String tagLine;

	private int sortOrder = Integer.MAX_VALUE;

	private String imagePath;

	public String getSiteUrl() {
		return this.siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public boolean isFeatured() {
		return this.featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public String getTagLine() {
		return this.tagLine;
	}

	public void setTagLine(String tagLine) {
		this.tagLine = tagLine;
	}

	public int getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getImagePath() {
		return this.imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
