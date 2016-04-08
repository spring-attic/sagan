package sagan.search.support;

import java.util.List;

import org.springframework.data.domain.Page;

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
        return facets;
    }
}
