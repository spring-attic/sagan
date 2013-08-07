package org.springframework.site.search;

import org.junit.Test;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SearchResultsTests {

	@Test
	public void movesProjectsToTheBottom() throws Exception {
		PageImpl<SearchResult> page = new PageImpl<>(new ArrayList<SearchResult>());
		ArrayList<SearchFacet> facets = new ArrayList<>();

		SearchFacet guides = new SearchFacet("guides", "Guides", 1);
		facets.add(guides);
		SearchFacet projects = new SearchFacet("projects", "Projects", 1);
		facets.add(projects);
		SearchFacet understanding = new SearchFacet("understanding", "Understanding", 1);
		facets.add(understanding);

		SearchResults searchResults = new SearchResults(page, facets);

		assertThat(searchResults.getFacets().get(0), equalTo(guides));
		assertThat(searchResults.getFacets().get(1), equalTo(understanding));
		assertThat(searchResults.getFacets().get(2), equalTo(projects));
	}

	@Test
	public void doesNothingIfProjectsFacetIsNotThere() throws Exception {
		PageImpl<SearchResult> page = new PageImpl<>(new ArrayList<SearchResult>());
		ArrayList<SearchFacet> facets = new ArrayList<>();

		SearchFacet guides = new SearchFacet("guides", "Guides", 1);
		facets.add(guides);
		SearchFacet understanding = new SearchFacet("understanding", "Understanding", 1);
		facets.add(understanding);

		SearchResults searchResults = new SearchResults(page, facets);

		assertThat(searchResults.getFacets().get(0), equalTo(guides));
		assertThat(searchResults.getFacets().get(1), equalTo(understanding));
	}
}
