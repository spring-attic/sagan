package org.springframework.site.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.site.search.SearchEntry;
import org.springframework.stereotype.Service;

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

}
