package org.springframework.site.search;

import java.util.ArrayList;
import java.util.List;

public class SearchFacet {
	private String path;
	private String name;
	private int count;
	private List<SearchFacet> facets;

	public SearchFacet(String path, String name, int count) {
		this(path, name, count, new ArrayList<SearchFacet>());
	}

	public SearchFacet(String path, String name, int count, List<SearchFacet> facets) {
		this.path = path;
		this.name = name;
		this.count = count;
		this.facets = facets;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public String getFullPath() {
		if (path.equals("")) {
			return name;
		}
		return path + "/" + name;
	}

	public int getCount() {
		return count;
	}

	public List<SearchFacet> getFacets() {
		return facets;
	}

	public boolean hasFacets() {
		return facets.size() > 0;
	}

	public String getLinkText() {
		return String.format("%s (%d)", name, count);
	}
}
