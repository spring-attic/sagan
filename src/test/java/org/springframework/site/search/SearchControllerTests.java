package org.springframework.site.search;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.search.SearchEntry;
import org.springframework.search.SearchService;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class SearchControllerTests {

	@Mock
	private SearchService searchService;

	private SearchController controller;
	private ExtendedModelMap model = new ExtendedModelMap();
	private Page<SearchEntry> resultsPage;
	private List<SearchEntry> entries = new ArrayList<SearchEntry>();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new SearchController(searchService);
		SearchEntry entry = new SearchEntry();
		entries.add(entry);
		resultsPage = new PageImpl<SearchEntry>(entries);
		when(searchService.search(anyString(), any(Pageable.class))).thenReturn(resultsPage);
	}

	@Test
	public void search_providesQueryInModel() {
		controller.search("searchTerm", 1, model);
		assertThat((String) model.get("query"), is(equalTo("searchTerm")));
	}

	@Test
	public void search_providesPaginationInfoInModel() {
		controller.search("searchTerm", 1, model);
		assertThat(model.get("paginationInfo"), is(notNullValue()));
	}

	@Test
	public void search_providesResultsInModel() {
		controller.search("searchTerm", 1, model);
		assertThat((List<SearchEntry>) model.get("results"), equalTo(entries));
	}

	@Test
	public void search_providesAllResultsForBlankQuery() {
		controller.search("", 1, model);
		assertThat((List<SearchEntry>) model.get("results"), equalTo(entries));
	}
}
