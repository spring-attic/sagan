package sagan.search.support;

import sagan.search.SearchException;

import java.util.Collections;

import org.junit.Test;

import org.springframework.data.domain.Pageable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class SearchServiceTests {

    private GoogleClient jestClient = mock(GoogleClient.class);
    private SearchService searchService = new SearchService(jestClient, mock(SearchResultParser.class));

    @SuppressWarnings("unchecked")
    public void throwsException() throws Exception {
        given(jestClient.execute(any(String.class), any(Integer.class))).willThrow(Exception.class);
    }

    @Test(expected = SearchException.class)
    public void searchExceptionHandling() throws Exception {
        throwsException();
        searchService.search("foo", mock(Pageable.class), Collections.<String> emptyList());
    }

}
