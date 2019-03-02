package sagan.search.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SearchResults {
    private Page<SearchResult> page;

    private final List<SearchFacet> facets;

    SearchResults() {
        this(null, null);
    }

    public SearchResults(Page<SearchResult> page, List<SearchFacet> facets) {
        this.page = page;
        this.facets = facets;
    }

    public Page<SearchResult> getPage() {
        return page;
    }

    public List<SearchFacet> getFacets() {
        return facets;
    }
}
