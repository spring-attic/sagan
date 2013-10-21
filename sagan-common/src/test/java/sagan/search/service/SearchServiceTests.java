package sagan.search.service;

import sagan.search.SearchEntry;
import sagan.search.SearchEntryBuilder;
import sagan.search.SearchException;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import org.springframework.data.domain.Pageable;

import io.searchbox.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class SearchServiceTests {

    private JestClient jestClient = mock(JestClient.class);
    private SearchService searchService = new SearchService(this.jestClient, mock(SearchResultParser.class));
    private SearchEntry entry;

    @Before
    public void setUp() throws Exception {
        this.entry = SearchEntryBuilder.entry().path("/some/path")
                .title("This week in Spring").rawContent("raw content")
                .summary("Html summary").publishAt("2013-01-01 10:00")
                .type("someType").build();
    }

    @SuppressWarnings("unchecked")
    public void throwsException() throws Exception {
        given(this.jestClient.execute(any(Action.class))).willThrow(Exception.class);
    }

    @Test(expected = SearchException.class)
    public void saveToIndexExceptionHandling() throws Exception {
        throwsException();
        this.searchService.saveToIndex(this.entry);
    }

    @Test(expected = SearchException.class)
    public void searchExceptionHandling() throws Exception {
        throwsException();
        this.searchService.search("foo", mock(Pageable.class), Collections.<String> emptyList());
    }

    @Test(expected = SearchException.class)
    public void removeFromIndexExceptionHandling() throws Exception {
        throwsException();
        this.searchService.removeFromIndex(this.entry);
    }

    @Test
    public void usesTheSearchEntriesType() throws Exception {
        given(this.jestClient.execute(any(Action.class))).willReturn(new JestResult());
        this.searchService.saveToIndex(this.entry);
        verify(jestClient).execute(argThat(new ArgumentMatcher<Action>() {
            @Override
            public boolean matches(Object item) {
                Index action = (Index) item;
                return action.getType().equals("someType");
            }
        }));
    }

    @Test
    public void handlesNullFilters() throws Exception {
        given(this.jestClient.execute(any(Action.class))).willReturn(new JestResult());
        this.searchService.search("foo", mock(Pageable.class), null);
    }
}
