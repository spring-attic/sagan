package org.springframework.site.search;

import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SearchServiceTests {

	private ElasticsearchOperations elasticsearch = mock(ElasticsearchOperations.class);
	private SearchService searchService = new SearchService(elasticsearch);

	@Test
	public void saveAnEntry() {
		searchService.saveToIndex(new SearchEntry());
		verify(elasticsearch).index(any(IndexQuery.class));
	}
}
