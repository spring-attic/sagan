package sagan.site.projects.admin;

import java.util.HashSet;
import java.util.Set;

public class ProjectFormInfo {

	public String id;

	public String displaySiteUrl;

	public boolean displayFeatured;

	public String displayTagLine;

	public int displaySortOrder;

	public String displayImagePath;

	public Set<String> groups = new HashSet<>();

	public String bootConfigSource;

	public String overviewSource;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplaySiteUrl() {
		return displaySiteUrl;
	}

	public void setDisplaySiteUrl(String displaySiteUrl) {
		this.displaySiteUrl = displaySiteUrl;
	}

	public boolean isDisplayFeatured() {
		return displayFeatured;
	}

	public void setDisplayFeatured(boolean displayFeatured) {
		this.displayFeatured = displayFeatured;
	}

	public String getDisplayTagLine() {
		return displayTagLine;
	}

	public void setDisplayTagLine(String displayTagLine) {
		this.displayTagLine = displayTagLine;
	}

	public int getDisplaySortOrder() {
		return displaySortOrder;
	}

	public void setDisplaySortOrder(int displaySortOrder) {
		this.displaySortOrder = displaySortOrder;
	}

	public String getDisplayImagePath() {
		return displayImagePath;
	}

	public void setDisplayImagePath(String displayImagePath) {
		this.displayImagePath = displayImagePath;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

	public String getBootConfigSource() {
		return bootConfigSource;
	}

	public void setBootConfigSource(String bootConfigSource) {
		this.bootConfigSource = bootConfigSource;
	}

	public String getOverviewSource() {
		return overviewSource;
	}

	public void setOverviewSource(String overviewSource) {
		this.overviewSource = overviewSource;
	}
}
