package org.springframework.site.indexer;

import org.jsoup.nodes.Document;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

import java.util.Date;

public class WebDocumentSearchEntryMapper implements SearchEntryMapper<WebDocument> {

	@Override
	public SearchEntry map(WebDocument document) {
		Document input = document.getDocument();
		SearchEntry entry = new SearchEntry();
		entry.setPublishAt(new Date(0L));
		String text = input.text();
		entry.setRawContent(text);
		entry.setSummary(text.substring(0, Math.min(500, text.length())));
		entry.setTitle(input.title());
		entry.setPath(document.getPath());
		return entry;
	}

}
