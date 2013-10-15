package sagan.search.web;

import java.util.ArrayList;
import java.util.List;

public class SearchForm {
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
