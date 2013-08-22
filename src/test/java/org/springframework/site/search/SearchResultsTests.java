package org.springframework.site.search;

import org.junit.Test;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

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

		SearchFacet rootFacet = new SearchResults(page, facets).getRootFacet();

		assertThat(rootFacet.getFacets().get(0), equalTo(guides));
		assertThat(rootFacet.getFacets().get(1), equalTo(understanding));
		assertThat(rootFacet.getFacets().get(2), equalTo(projects));
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

		assertThat(searchResults.getRootFacet().getFacets().get(0), equalTo(guides));
		assertThat(searchResults.getRootFacet().getFacets().get(1), equalTo(understanding));
	}

	@Test
	public void movesReferenceToBeAHeaderFacetUnderProjects() throws Exception {
		PageImpl<SearchResult> page = new PageImpl<>(new ArrayList<SearchResult>());
		ArrayList<SearchFacet> facets = new ArrayList<>();

		SearchFacet projects = new SearchFacet("Projects", "Projects", 1);
		facets.add(projects);
		SearchFacet api = new SearchFacet("Projects/Api", "Api", 1);
		projects.getFacets().add(api);
		SearchFacet springFramework = new SearchFacet("Projects/SpringFramework", "Spring Framework", 1);
		projects.getFacets().add(springFramework);

		SearchResults searchResults = new SearchResults(page, facets);

		SearchFacet projectFacet = searchResults.getRootFacet().getFacets().get(0);
		assertThat(projectFacet.getHeaderFacets(), contains(api));
		assertThat(projectFacet.getFacets(), contains(springFramework));
	}

	@Test
	public void movesApiToBeAHeaderFacetUnderProjects() throws Exception {
		PageImpl<SearchResult> page = new PageImpl<>(new ArrayList<SearchResult>());
		ArrayList<SearchFacet> facets = new ArrayList<>();

		SearchFacet projects = new SearchFacet("Projects", "Projects", 1);
		facets.add(projects);
		SearchFacet reference = new SearchFacet("Projects/Reference", "Reference", 1);
		projects.getFacets().add(reference);
		SearchFacet springFramework = new SearchFacet("Projects/SpringFramework", "Spring Framework", 1);
		projects.getFacets().add(springFramework);

		SearchResults searchResults = new SearchResults(page, facets);

		SearchFacet projectFacet = searchResults.getRootFacet().getFacets().get(0);
		assertThat(projectFacet.getHeaderFacets(), contains(reference));
		assertThat(projectFacet.getFacets(), contains(springFramework));
	}
}
