package org.springframework.site.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class SearchResultParser {

	public SearchResults parseResults(JestResult jestResult, Pageable pageable) {
		JsonObject jsonObject = jestResult.getJsonObject();
		JsonObject hits = jsonObject.getAsJsonObject("hits");
		JsonArray resultsArray = hits.getAsJsonArray("hits");

		ArrayList<SearchResult> results = prepareResults(resultsArray);


		int totalResults = hits.get("total").getAsInt();
		PageImpl<SearchResult> page = new PageImpl<>(results, pageable, totalResults);

		return new SearchResults(page, Collections.<SearchFacet>emptyList());
	}

	private ArrayList<SearchResult> prepareResults(JsonArray hits) {
		ArrayList<SearchResult> results = new ArrayList<>();
		for (JsonElement element : hits) {
			JsonObject hit = element.getAsJsonObject();
			String summary = extractSummary(hit);
			String id = hit.get("_id").getAsString();

			JsonObject source = hit.getAsJsonObject("_source");
			String title = source.get("title").getAsString();
			String url = source.get("path").getAsString();

			SearchResult result = new SearchResult(id, title, summary, url);
			results.add(result);
		}
		return results;
	}

	private String extractSummary(JsonObject hit) {
		String summary;
		JsonObject highlight = hit.getAsJsonObject("highlight");
		if (highlight == null) {
			JsonObject source = hit.getAsJsonObject("_source");
			summary = source.get("summary").getAsString();
		} else {
			JsonArray rawContent = highlight.getAsJsonArray("rawContent");
			summary = rawContent.get(0).getAsString();
		}
		return summary;
	}
}
