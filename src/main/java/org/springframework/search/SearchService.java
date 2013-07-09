package org.springframework.search;

import io.searchbox.Action;
import io.searchbox.Parameters;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

	private static final String INDEX = "site";

	private final SearchQueryBuilder searchQueryBuilder = new SearchQueryBuilder();
	private final JestClient jestClient;

	private boolean useRefresh = false;

	@Autowired
	public SearchService(JestClient jestClient) {
		this.jestClient = jestClient;
	}

	public void saveToIndex(SearchEntry entry) {
		Index newIndex = new Index.Builder(entry)
				.id(entry.getId())
				.index(INDEX)
				.type("site") //TODO this should come from the 'entry'
				.build();

		if (useRefresh) {
			newIndex.addParameter(Parameters.REFRESH, true);
		}
		execute(newIndex);
	}

	private JestResult execute(Action action) {
		try {
			return jestClient.execute(action);
		} catch (Exception e) {
			throw new SearchException(e);
		}
	}

	public Page<SearchEntry> search(String term, Pageable pageable) {
		Search search;
		if (term.equals("")) {
			search = searchQueryBuilder.forEmptyQuery(pageable);
		} else {
			search = searchQueryBuilder.forQuery(term, pageable);
		}
		search.addIndex(INDEX);
		JestResult jestResult = execute(search);
		List<SearchEntry> searchEntries = jestResult.getSourceAsObjectList(SearchEntry.class);
		return new PageImpl<SearchEntry>(searchEntries);
	}

	public void deleteIndex()  {
		execute(new DeleteIndex(INDEX));
	}

	public void createIndex() {
		execute(new CreateIndex(INDEX));
	}

	public void setUseRefresh(boolean useRefresh) {
		this.useRefresh = useRefresh;
	}
}
