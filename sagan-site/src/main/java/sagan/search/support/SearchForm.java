package sagan.search.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Form-backing object used during POST requests to /search.
 *
 * @see SearchController
 */
class SearchForm {
    private String q = "";
    private List<String> filters = new ArrayList<>();

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }
}
