package org.springframework.site.search;

import org.springframework.data.domain.Page;

import java.util.List;

public class SearchResults {
	private Page<SearchResult> page;
	private final List<SearchFacet> facets;

	public SearchResults(Page<SearchResult> page, List<SearchFacet> facets) {
		this.page = page;
		this.facets = facets;
	}

	public Page<SearchResult> getPage() {
		return page;
	}

	public List<SearchFacet> getFacets() {
		SearchFacet project = null;
		for (SearchFacet facet : facets) {
			if (facet.getName().equals("Projects")) {
				project = facet;
				break;
			}
		}

		if (project != null) {
			facets.remove(project);
			facets.add(project);
		}

		return facets;
	}

	public boolean hasFacets() {
		return facets.size() > 0;
	}
}
