package org.springframework.site.web.search;

import io.searchbox.Action;
import io.searchbox.client.JestClient;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchException;
import org.springframework.site.search.SearchService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class SearchServiceTests {

	private JestClient jestClient = mock(JestClient.class);
	private SearchService searchService = new SearchService(this.jestClient);
	private SearchEntry entry;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		this.entry = SearchEntryBuilder.entry().path("/some/path")
				.title("This week in Spring").rawContent("raw content")
				.summary("Html summary").publishAt("2013-01-01 10:00").build();

		given(this.jestClient.execute(any(Action.class))).willThrow(Exception.class);
	}

	@Test(expected = SearchException.class)
	public void saveToIndexExceptionHandling() throws Exception {
		this.searchService.saveToIndex(this.entry);
	}

	@Test(expected = SearchException.class)
	public void searchExceptionHandling() throws Exception {
		this.searchService.search("foo", mock(Pageable.class));
	}

	@Test(expected = SearchException.class)
	public void removeFromIndexExceptionHandling() throws Exception {
		this.searchService.removeFromIndex(this.entry);
	}

	@Test(expected = SearchException.class)
	public void createIndexExceptionHandling() throws Exception {
		this.searchService.createIndex();
	}

	@Test(expected = SearchException.class)
	public void deleteIndexExceptionHandling() throws Exception {
		this.searchService.deleteIndex();
	}

}
