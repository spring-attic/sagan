package org.springframework.site.search;

import java.util.Collections;
import java.util.List;

public class SearchFacet {
	private String name;
	private int count;
	private List<SearchFacet> facets;

	public SearchFacet(String name, int count) {
		this(name, count, Collections.<SearchFacet>emptyList());
	}

	public SearchFacet(String name, int count, List<SearchFacet> facets) {
		this.name = name;
		this.count = count;
		this.facets = facets;
	}

	public String getName() {
		return name;
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
