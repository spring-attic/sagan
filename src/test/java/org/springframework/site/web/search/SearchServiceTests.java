package org.springframework.site.web.search;

import io.searchbox.Action;
import io.searchbox.client.JestClient;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchException;
import org.springframework.site.search.SearchService;

import static org.mockito.BDDMockito.*;

public class SearchServiceTests {

	private JestClient jestClient = mock(JestClient.class);
	private SearchService searchService = new SearchService(jestClient);
	private SearchEntry entry;

	@Before
	public void setUp() throws Exception {
		entry = SearchEntryBuilder.entry()
				.path("/some/path")
				.title("This week in Spring")
				.rawContent("raw content")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.build();

		given(jestClient.execute(any(Action.class))).willThrow(Exception.class);
	}

	@Test(expected = SearchException.class)
	public void saveToIndexExceptionHandling() throws Exception {
		searchService.saveToIndex(entry);
	}

	@Test(expected = SearchException.class)
	public void searchExceptionHandling() throws Exception {
		searchService.search("foo", mock(Pageable.class));
	}

	@Test(expected = SearchException.class)
	public void removeFromIndexExceptionHandling() throws Exception {
		searchService.removeFromIndex(entry);
	}

	@Test(expected = SearchException.class)
	public void createIndexExceptionHandling() throws Exception {
		searchService.createIndex();
	}

	@Test(expected = SearchException.class)
	public void deleteIndexExceptionHandling() throws Exception {
		searchService.deleteIndex();
	}


}
