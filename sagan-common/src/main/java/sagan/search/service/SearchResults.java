package sagan.search.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResults {
    @JsonDeserialize(builder = PageBuilder.class)
    private SearchPage page;

    private final List<SearchFacet> facets;

    SearchResults() {
        this(null, null);
    }

    @SuppressWarnings("deprecation")
    public SearchResults(Page<SearchResult> page, List<SearchFacet> facets) {
        if (page == null) {
            this.page = new SearchPage(Collections.emptyList());
        } else {
            if (page.getSize() < 1) {
                this.page = new SearchPage(page.getContent());
            } else {
                // N.B. need to use deprecated constructor here while search service is using old version
                this.page = new SearchPage(page.getContent(), new PageRequest(page.getNumber(), page.getSize()), page
                        .getTotalElements());
            }
        }
        this.facets = facets;
    }

    public Page<SearchResult> getPage() {
        return page;
    }

    public List<SearchFacet> getFacets() {
        return facets;
    }

    @SuppressWarnings("serial")
    @JsonDeserialize(builder = PageBuilder.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchPage extends PageImpl<SearchResult> {

        public SearchPage(List<SearchResult> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }

        public SearchPage(List<SearchResult> content) {
            super(content);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageBuilder {
        private List<SearchResult> content = Collections.emptyList();
        private int size;
        private int page;
        private long total;

        public PageBuilder withContent(List<SearchResult> content) {
            this.content = content;
            return this;
        }

        public PageBuilder withSize(int size) {
            this.size = size;
            return this;
        }

        public PageBuilder withTotalElements(int total) {
            this.total = total;
            return this;
        }

        public PageBuilder withNumber(int page) {
            this.page = page;
            return this;
        }

        @SuppressWarnings("deprecation")
        public SearchPage build() {
            // N.B. need to use deprecated constructor here while search service is using old version
            return new SearchPage(this.content, new PageRequest(page, size), total);
        }
    }

}
