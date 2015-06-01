package sagan.search.support;

import com.google.gson.Gson;
import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.data.domain.Pageable;
import sagan.search.SearchException;
import sagan.search.types.SearchEntry;
import sagan.search.types.SearchType;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SearchServiceTests {

    private JestClient jestClient = mock(JestClient.class);
    private SearchService searchService = new SearchService(jestClient, mock(SearchResultParser.class), new Gson());
    private SearchEntry entry;

    @Before
    public void setUp() throws Exception {
        entry = SearchEntryBuilder.entry().path("/some/path")
                .title("This week in Spring").rawContent("raw content")
                .summary("Html summary").publishAt("2013-01-01 10:00")
                .blog();
    }

    @SuppressWarnings("unchecked")
    public void throwsException() throws Exception {
        given(jestClient.execute(any(Action.class))).willThrow(Exception.class);
    }

    @Test(expected = SearchException.class)
    public void saveToIndexExceptionHandling() throws Exception {
        throwsException();
        searchService.saveToIndex(entry);
    }

    @Test(expected = SearchException.class)
    public void searchExceptionHandling() throws Exception {
        throwsException();
        searchService.search("foo", mock(Pageable.class), Collections.<String> emptyList());
    }

    @Test(expected = SearchException.class)
    public void removeFromIndexExceptionHandling() throws Exception {
        throwsException();
        searchService.removeFromIndex(entry);
    }

    @Test
    public void usesTheSearchEntriesType() throws Exception {
        given(jestClient.execute(any(Action.class))).willReturn(mock(JestResult.class));
        searchService.saveToIndex(entry);
        verify(jestClient).execute(argThat(new ArgumentMatcher<Action>() {
            @Override
            public boolean matches(Object item) {
                Index action = (Index) item;
                return action.getType().equals(SearchType.BLOG_POST.toString());
            }
        }));
    }

    @Test
    public void handlesNullFilters() throws Exception {
        given(jestClient.execute(any(Action.class))).willReturn(mock(JestResult.class));
        searchService.search("foo", mock(Pageable.class), null);
    }
}
