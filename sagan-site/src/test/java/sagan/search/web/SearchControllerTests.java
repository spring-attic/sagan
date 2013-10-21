package sagan.search.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ExtendedModelMap;

import sagan.search.SearchFacet;
import sagan.search.SearchResult;
import sagan.search.SearchResults;
import sagan.search.service.SearchService;
import sagan.search.web.SearchController;
import sagan.search.web.SearchForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

public class SearchControllerTests {

    @Mock
    private SearchService searchService;

    private SearchController controller;
    private ExtendedModelMap model = new ExtendedModelMap();
    private Page<SearchResult> resultsPage;
    private List<SearchResult> entries = new ArrayList<>();
    private SearchForm searchForm = new SearchForm();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.controller = new SearchController(this.searchService);
        SearchResult entry = new SearchResult("", "", "", "", "", "", "",
                "original search term");
        this.entries.add(entry);
        this.resultsPage = new PageImpl<>(this.entries);
        given(this.searchService.search(anyString(), (Pageable) anyObject(), anyList()))
                .willReturn(
                        new SearchResults(this.resultsPage, Collections
                                .<SearchFacet> emptyList()));
    }

    @Test
    public void search_providesQueryInModel() {
        this.searchForm.setQ("searchTerm");
        this.controller.search(this.searchForm, 1, this.model);
        assertThat((SearchForm) this.model.get("searchForm"), equalTo(this.searchForm));
    }

    @Test
    public void search_providesPaginationInfoInModel() {
        this.searchForm.setQ("searchTerm");
        this.controller.search(this.searchForm, 1, this.model);
        assertThat(this.model.get("paginationInfo"), is(notNullValue()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void search_providesResultsInModel() {
        this.searchForm.setQ("searchTerm");
        this.controller.search(this.searchForm, 1, this.model);
        assertThat((List<SearchResult>) this.model.get("results"), equalTo(this.entries));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void search_providesAllResultsForBlankQuery() {
        this.searchForm.setQ("");
        this.controller.search(this.searchForm, 1, this.model);
        assertThat((List<SearchResult>) this.model.get("results"), equalTo(this.entries));
    }

}
