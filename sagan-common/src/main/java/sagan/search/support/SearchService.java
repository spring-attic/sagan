package sagan.search.support;

import sagan.search.SearchEntry;
import sagan.search.SearchException;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.searchbox.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

@Service
public class SearchService {

    private static Log logger = LogFactory.getLog(SearchService.class);

    private final SearchQueryBuilder searchQueryBuilder = new SearchQueryBuilder();
    private final JestClient jestClient;
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    private boolean useRefresh = false;
    private SearchResultParser searchResultParser;

    @Value("${elasticsearch.client.index}")
    private String index;

    @Autowired
    public SearchService(JestClient jestClient, SearchResultParser searchResultParser) {
        this.jestClient = jestClient;
        this.searchResultParser = searchResultParser;
    }

    public void saveToIndex(SearchEntry entry) {
        Index.Builder indexEntryBuilder = new Index.Builder(entry).id(entry.getId()).index(index).type(entry.getType());

        if (useRefresh) {
            indexEntryBuilder.refresh(true);
        }
        logger.debug("Indexing " + entry.getPath());
        execute(indexEntryBuilder.build());
    }

    public SearchResults search(String term, Pageable pageable, List<String> filter) {
        Search.Builder searchBuilder;
        if (term.equals("")) {
            searchBuilder = searchQueryBuilder.forEmptyQuery(pageable, filter);
        } else {
            searchBuilder = searchQueryBuilder.forQuery(term, pageable, filter);
        }
        searchBuilder.addIndex(index);
        Search search = searchBuilder.build();
        logger.debug(search.getData());
        JestResult jestResult = execute(search);
        return searchResultParser.parseResults(jestResult, pageable, term);
    }

    public void setUseRefresh(boolean useRefresh) {
        this.useRefresh = useRefresh;
    }

    public void removeFromIndex(SearchEntry entry) {
        Delete delete = new Delete.Builder()
                .id(entry.getId())
                .index(index)
                // TODO this should come from the 'entry'
                .type("site")
                .build();

        execute(delete);
    }

    public void removeOldProjectEntriesFromIndex(String projectId, List<String> supportedVersions) {
        String query = deleteQueryBuilder.unsupportedProjectEntriesQuery(projectId, supportedVersions);

        execute(new DeleteByQuery.Builder(query).build());
    }

    private JestResult execute(Action action) {
        try {
            JestResult result = jestClient.execute(action);
            logger.debug(result.getJsonString());
            if(!result.isSucceeded()) {
                logger.warn("Failed to execute Elastic Search action: " + result.getErrorMessage());
            }
            return result;
        } catch (Exception e) {
            throw new SearchException(e);
        }
    }
}
