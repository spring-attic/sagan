package io.spring.site.search;

import io.searchbox.core.Search;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public class SearchQueryBuilder {
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
		String formattedDate = ISODateTimeFormat.dateTimeNoMillis().print(new Date().getTime());
		RangeFilterBuilder rangeFilterBuilder = new RangeFilterBuilder("publishAt")
				.to(formattedDate)
				.includeLower(true)
				.includeUpper(true);

		filterBuilder.add(rangeFilterBuilder);
	}

	private void filterFacets(List<String> filters, AndFilterBuilder filterBuilder) {
		if (filters != null && !filters.isEmpty()) {
			TermsFilterBuilder facetFilter = new TermsFilterBuilder("facetPaths", filters).execution("or");
			filterBuilder.add(facetFilter);
		}
	}

	private void addFacetPathsResult(SearchSourceBuilder searchSourceBuilder) {
		TermsFacetBuilder facetBuilder = new TermsFacetBuilder("facet_paths_result")
				.field("facetPaths")
				.order(TermsFacet.ComparatorType.TERM).size(100000);

		searchSourceBuilder.facet(facetBuilder);
	}

	private void addHighlights(SearchSourceBuilder searchSourceBuilder) {
		HighlightBuilder highlightBuilder = new HighlightBuilder()
				.order("score")
				.requireFieldMatch(false)
				.field("rawContent", 300, 1);

		searchSourceBuilder.highlight(highlightBuilder);
	}

}