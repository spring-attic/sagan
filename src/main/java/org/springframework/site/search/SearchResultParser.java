package org.springframework.site.search;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SearchResultParser {

	public SearchResults parseResults(JestResult jestResult, Pageable pageable, String originalSearchTerm) {
		JsonObject response = jestResult.getJsonObject();
		JsonObject hits = response.getAsJsonObject("hits");
		JsonArray resultsArray = hits.getAsJsonArray("hits");

		ArrayList<SearchResult> results = prepareResults(resultsArray, originalSearchTerm);

		int totalResults = hits.get("total").getAsInt();
		PageImpl<SearchResult> page = new PageImpl<>(results, pageable, totalResults);

		List<SearchFacet> facets = prepareFacets(response);
		return new SearchResults(page, facets);
	}

	private List<SearchFacet> prepareFacets(JsonObject response) {
		JsonObject facets = response.getAsJsonObject("facets");
		if (facets == null) {
			return Collections.emptyList();
		}
		JsonObject pathsResult = facets.getAsJsonObject("facet_paths_result");
		List<JsonElement> terms = sortedTermElements(pathsResult.getAsJsonArray("terms"));

		SearchFacetBuilder builder = new SearchFacetBuilder();

		for (JsonElement element : terms) {
			JsonObject jsonObject = element.getAsJsonObject();
			String term = jsonObject.get("term").getAsString();
			int count = jsonObject.get("count").getAsInt();
			builder.addTerm(term, count);
		}

		return builder.build();
	}

	private List<JsonElement> sortedTermElements(JsonArray termArray) {
		ArrayList<JsonElement> list = Lists.newArrayList(termArray);
		Collections.sort(list, new Comparator<JsonElement>() {
			@Override
			public int compare(JsonElement o1, JsonElement o2) {
				String term1 = o1.getAsJsonObject().get("term").getAsString();
				String term2 = o2.getAsJsonObject().get("term").getAsString();
				return term1.compareTo(term2);
			}
		});
		return list;
	}

	private ArrayList<SearchResult> prepareResults(JsonArray hits, String originalSearchTerm) {
		ArrayList<SearchResult> results = new ArrayList<>();
		for (JsonElement element : hits) {
			JsonObject hit = element.getAsJsonObject();
			String id = hit.get("_id").getAsString();

			JsonObject source = hit.getAsJsonObject("_source");
			String title = source.get("title").getAsString();
			String url = source.get("path").getAsString();
			String type = hit.get("_type").getAsString();
			String summary = source.get("summary").getAsString();
			String subtitle = safelyLookupFieldString(source, "subTitle");

			String highlight = extractHighlight(hit);

			SearchResult result = new SearchResult(id, title, subtitle, summary, url, type, highlight, originalSearchTerm);
			results.add(result);
		}
		return results;
	}

	private String safelyLookupFieldString(JsonObject source, String field) {
		JsonElement fieldElement = source.get(field);
		return (fieldElement != null) ? fieldElement.getAsString() : "";
	}

	private String extractHighlight(JsonObject hit) {
		String summary = null;
		JsonObject highlight = hit.getAsJsonObject("highlight");

		if (highlight != null) {
			JsonArray rawContent = highlight.getAsJsonArray("rawContent");
			summary =  rawContent.get(0).getAsString();
		}

		return summary;
	}
}
