package org.springframework.site.web.search;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;

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
		given(searchService.search(anyString(), (Pageable) anyObject())).willReturn(resultsPage);
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
