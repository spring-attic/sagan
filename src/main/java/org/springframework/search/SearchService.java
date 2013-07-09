package org.springframework.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.Date;

import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

@Service
public class SearchService {

	private ElasticsearchOperations elasticsearchOperations;

	@Autowired
	public SearchService(ElasticsearchOperations elasticsearchOperations) {
		this.elasticsearchOperations = elasticsearchOperations;
	}

	public void saveToIndex(SearchEntry entry) {
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId(entry.getId());
		indexQuery.setObject(entry);
		elasticsearchOperations.index(indexQuery);
		elasticsearchOperations.refresh(SearchEntry.class, false);
	}

	public Page<SearchEntry> search(String query, Pageable pageable) {
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(matchAllQuery());
		if (!query.equals("")) {
			searchQueryBuilder
					.withFilter(
							andFilter(
									numericRangeFilter("publishAt").lte(new Date().getTime()),
									orFilter(
											queryFilter(matchPhraseQuery("title", query)),
											queryFilter(matchPhraseQuery("rawContent", query))
									)
							)
					);
		}
		SearchQuery searchQuery = searchQueryBuilder.build();
		searchQuery.setPageable(pageable);
		return elasticsearchOperations.queryForPage(searchQuery, SearchEntry.class);
	}

	public void deleteIndex() {
		elasticsearchOperations.deleteIndex(SearchEntry.class);
	}
}
