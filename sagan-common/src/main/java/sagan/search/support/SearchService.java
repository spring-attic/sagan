package sagan.search.support;

import sagan.search.SearchException;
import sagan.search.types.SearchEntry;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final GoogleClient jestClient;

    private SearchResultParser searchResultParser;

    @Autowired
    public SearchService(GoogleClient jestClient, SearchResultParser searchResultParser) {
        this.jestClient = jestClient;
        this.searchResultParser = searchResultParser;
    }

    public void saveToIndex(SearchEntry entry) {
    }

    public SearchResults search(String term, Pageable pageable, List<String> filter) {
        try {
            GoogleResult jestResult = this.jestClient.execute(term, pageable.getPageNumber());
            return searchResultParser.parseResults(jestResult, pageable, term);
        } catch (Exception e) {
            throw new SearchException(e);
        }
    }

    public void setUseRefresh(boolean useRefresh) {
    }

    public void removeFromIndex(SearchEntry entry) {
    }

    public void removeOldProjectEntriesFromIndex(String projectId, List<String> supportedVersions) {
    }

}
