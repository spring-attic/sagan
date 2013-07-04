package org.springframework.site.search;

import org.jsoup.nodes.Document;

import java.util.Date;

public class WebDocumentSearchEntryMapper implements SearchEntryMapper<WebDocument> {

	@Override
	public SearchEntry map(WebDocument document) {
		Document input = document.getDocument();
		SearchEntry entry = new SearchEntry();
		entry.setId(document.getPath());
		entry.setPublishAt(new Date(0L));
		String text = input.text();
		entry.setRawContent(text);
		entry.setSummary(text.substring(0, Math.min(500, text.length())));
		entry.setTitle(input.title());
		entry.setPath(document.getPath());
		return entry;
	}

}
