package integration.stubs;

import io.searchbox.client.JestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchResult;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;

@Service
@Primary
@SuppressWarnings("unused")
//Component Scanned
public class StubSearchService extends SearchService {

	@Autowired
	public StubSearchService(JestClient jestClient) {
		super(null, null);
	}

	@Override
	public void saveToIndex(SearchEntry entry) {
		System.out.println("foo");
	}

	@Override
	public Page<SearchResult> search(String term, Pageable pageable) {
		return null;
	}

	@Override
	public void setUseRefresh(boolean useRefresh) {
	}

	@Override
	public void removeFromIndex(SearchEntry entry) {
	}
}
