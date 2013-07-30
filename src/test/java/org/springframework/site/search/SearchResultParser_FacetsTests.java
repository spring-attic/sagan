package org.springframework.site.search;

import com.google.gson.JsonParser;
import io.searchbox.client.JestResult;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SearchResultParser_FacetsTests {

	private static String RESULT_STRING = "{\n" +
			"  \"took\": 185,\n" +
			"  \"timed_out\": false,\n" +
			"  \"_shards\": {\n" +
			"    \"total\": 1,\n" +
			"    \"successful\": 1,\n" +
			"    \"failed\": 0\n" +
			"  },\n" +
			"  \"hits\": {\n" +
			"    \"total\": 0,\n" +
			"    \"max_score\": 0.5340888,\n" +
			"    \"hits\": []\n" +
			"  },\n" +
			"  \"facets\": {\n" +
			"    \"facet_paths_result\": {\n" +
			"      \"_type\": \"terms\",\n" +
			"      \"missing\": 0,\n" +
			"      \"total\": 8,\n" +
			"      \"other\": 0,\n" +
			"      \"terms\": [{\n" +
			"        \"term\": \"Guides\",\n" +
			"        \"count\": 3\n" +
			"      }, {\n" +
			"        \"term\": \"Guides/GettingStarted\",\n" +
			"        \"count\": 2\n" +
			"      }, {\n" +
			"        \"term\": \"Guides/Tutorials\",\n" +
			"        \"count\": 1\n" +
			"      }, {\n" +
			"        \"term\": \"Blog/Engineering\",\n" +
			"        \"count\": 1\n" +
			"      }, {\n" +
			"        \"term\": \"Blog\",\n" +
			"        \"count\": 1\n" +
			"      }]\n" +
			"    }\n" +
			"  }\n" +
			"}\n";

	private SearchResultParser searchResultParser;
	private SearchResults searchResults;

	@Before
	public void setup() {
		JsonParser jsonParser = new JsonParser();
		searchResultParser = new SearchResultParser();
		JestResult jestResult = new JestResult();
		jestResult.setJsonObject(jsonParser.parse(RESULT_STRING).getAsJsonObject());

		Pageable pageable = new PageRequest(1, 10);
		searchResults = searchResultParser.parseResults(jestResult, pageable);
	}

	@Test
	@Ignore
	public void returnTopLevelFacets() {
		assertThat(searchResults.getFacets().size(), equalTo(2));
	}

}
