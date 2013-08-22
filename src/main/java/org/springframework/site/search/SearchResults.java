package org.springframework.site.search;

import org.springframework.data.domain.Page;

import java.util.List;

public class SearchResults {
	private final SearchFacet root;
	private Page<SearchResult> page;

	public SearchResults(Page<SearchResult> page, List<SearchFacet> facets) {
		this.page = page;
		setupProjectsFacet(facets);
		root = new SearchFacet("", "", 0, facets);
	}

	private void setupProjectsFacet(List<SearchFacet> facets) {
		SearchFacet project = getFacetWithName(facets, "Projects");
		if (project != null) {
			facets.remove(project);
			facets.add(project);
			moveFacetToHeader(project, "Api");
			moveFacetToHeader(project, "Reference");
		}
	}

	private SearchFacet getFacetWithName(List<SearchFacet> facets, String facetName) {
		for (SearchFacet facet : facets) {
			if (facet.getName().equalsIgnoreCase(facetName)) {
				return facet;
			}
		}
		return null;
	}

	private void moveFacetToHeader(SearchFacet project, String facetName) {
		SearchFacet facet = getFacetWithName(project.getFacets(), facetName);
		if (facet != null) {
			project.getFacets().remove(facet);
			project.addHeaderFacet(facet);
		}
	}

	public Page<SearchResult> getPage() {
		return page;
	}

	public SearchFacet getRootFacet() {
		return root;
	}
}
