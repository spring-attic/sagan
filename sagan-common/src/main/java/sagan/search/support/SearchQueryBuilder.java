package sagan.search.support;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;

import org.springframework.data.domain.Pageable;

import io.searchbox.core.Search;

class SearchQueryBuilder {
    private static final String BOOST_CURRENT_VERSION_SCRIPT = "_score * (_source.current ? 1.1 : 1.0)";
    private static final String BOOSTED_TITLE_FIELD = "title^10";
    private static final String RAW_CONTENT_FIELD = "rawContent";

    SearchQueryBuilder() {
    }

    Search.Builder forEmptyQuery(Pageable pageable, List<String> filters) {
        QueryBuilder query = QueryBuilders.boolQuery().should(QueryBuilders.matchAllQuery());

        String search = buildSearch(query, filters, pageable);
        return new Search.Builder(search);
    }

    Search.Builder forQuery(String queryTerm, Pageable pageable, List<String> filters) {
        QueryBuilder query = QueryBuilders.multiMatchQuery(queryTerm, BOOSTED_TITLE_FIELD, RAW_CONTENT_FIELD);
        query = QueryBuilders.customScoreQuery(query).script(BOOST_CURRENT_VERSION_SCRIPT);

        String search = buildSearch(query, filters, pageable);
        return new Search.Builder(search);
    }

    private String buildSearch(QueryBuilder query, List<String> filters, Pageable pageable) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        addPagination(pageable, searchSourceBuilder);
        addQuery(query, searchSourceBuilder);
        addFilters(filters, searchSourceBuilder);
        addFacetPathsResult(searchSourceBuilder);
        addHighlights(searchSourceBuilder);

        return searchSourceBuilder.toString();
    }

    private void addPagination(Pageable pageable, SearchSourceBuilder searchSourceBuilder) {
        searchSourceBuilder.from(pageable.getOffset());
        searchSourceBuilder.size(pageable.getPageSize());
    }

    private SearchSourceBuilder addQuery(QueryBuilder query, SearchSourceBuilder searchSourceBuilder) {
        return searchSourceBuilder.query(query);
    }

    private void addFilters(List<String> filters, SearchSourceBuilder searchSourceBuilder) {
        AndFilterBuilder andFilterBuilder = new AndFilterBuilder();
        filterFutureResults(andFilterBuilder);
        filterFacets(filters, andFilterBuilder);
        searchSourceBuilder.filter(andFilterBuilder);
    }

    private void filterFutureResults(AndFilterBuilder filterBuilder) {
        String formattedDate =
                ISODateTimeFormat.dateTimeNoMillis().print(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        RangeFilterBuilder rangeFilterBuilder =
                new RangeFilterBuilder("publishAt").to(formattedDate).includeLower(true).includeUpper(true);

        filterBuilder.add(rangeFilterBuilder);
    }

    private void filterFacets(List<String> filters, AndFilterBuilder filterBuilder) {
        if (filters != null && !filters.isEmpty()) {

            Map<String, List<String>> splitFilters = splitFilters(filters);
            List<String> projects = splitFilters.get("projects");
            List<String> apiRef = splitFilters.get("apiRef");
            List<String> otherFacetPaths = splitFilters.get("others");

            AndFilterBuilder projectApiRefAnded = new AndFilterBuilder();
            if (apiRef.size() > 0) {
                projectApiRefAnded.add(new TermsFilterBuilder("facetPaths", apiRef).execution("or"));
            }

            if (projects.size() > 0) {
                projectApiRefAnded.add(new TermsFilterBuilder("facetPaths", projects).execution("or"));
            }

            OrFilterBuilder outermostFilter = new OrFilterBuilder();
            outermostFilter.add(projectApiRefAnded);
            if (otherFacetPaths.size() > 0) {
                outermostFilter.add(new TermsFilterBuilder("facetPaths", otherFacetPaths).execution("or"));
            }

            filterBuilder.add(outermostFilter);
        }
    }

    private Map<String, List<String>> splitFilters(List<String> filters) {
        ArrayList<String> projects = new ArrayList<>();
        ArrayList<String> apiRef = new ArrayList<>();
        ArrayList<String> others = new ArrayList<>();

        for (String filter : filters) {
            if (filter.startsWith("Projects")) {
                if (filter.equals("Projects/Api") || filter.equals("Projects/Reference")) {
                    apiRef.add(filter);
                } else {
                    projects.add(filter);
                }
            } else {
                others.add(filter);
            }
        }

        HashMap<String, List<String>> splitFilters = new HashMap<>();
        splitFilters.put("projects", projects);
        splitFilters.put("apiRef", apiRef);
        splitFilters.put("others", others);
        return splitFilters;
    }

    private void addFacetPathsResult(SearchSourceBuilder searchSourceBuilder) {
        TermsFacetBuilder facetBuilder = new TermsFacetBuilder("facet_paths_result")
                .field("facetPaths")
                .order(TermsFacet.ComparatorType.TERM).size(100000);

        searchSourceBuilder.facet(facetBuilder);
    }

    private void addHighlights(SearchSourceBuilder searchSourceBuilder) {
        HighlightBuilder highlightBuilder =
                new HighlightBuilder().order("score").requireFieldMatch(false).field("rawContent", 300, 1);

        searchSourceBuilder.highlight(highlightBuilder);
    }

}
