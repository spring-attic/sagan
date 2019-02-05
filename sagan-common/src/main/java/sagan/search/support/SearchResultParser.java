package sagan.search.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
class SearchResultParser {

    public SearchResults parseResults(GoogleResult jestResult, Pageable pageable, String originalSearchTerm) {
        JsonObject response = jestResult.getJsonObject();
        JsonArray resultsArray = response.getAsJsonArray("items");

        ArrayList<SearchResult> results = prepareResults(resultsArray, originalSearchTerm);

        int totalResults = response.get("queries").getAsJsonObject().get("request").getAsJsonArray().get(0)
                .getAsJsonObject().get("totalResults").getAsInt();
        PageImpl<SearchResult> page = new PageImpl<>(results, pageable, totalResults);

        List<SearchFacet> facets = Collections.emptyList();
        return new SearchResults(page, facets);
    }

    private ArrayList<SearchResult> prepareResults(JsonArray hits, String originalSearchTerm) {
        ArrayList<SearchResult> results = new ArrayList<>();
        for (JsonElement element : hits) {
            JsonObject hit = element.getAsJsonObject();
            String id = UUID.randomUUID().toString();

            JsonObject source = hit;
            String title = source.get("title").getAsString();
            String url = source.get("link").getAsString();
            String type = hit.get("kind").getAsString();
            String summary = source.get("snippet").getAsString();
            String subtitle = "";

            String highlight = null;

            SearchResult result =
                    new SearchResult(id, title, subtitle, summary, url, type, highlight, originalSearchTerm);
            results.add(result);
        }
        return results;
    }

}
