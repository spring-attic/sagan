package org.springframework.site.indexer;

import org.jsoup.nodes.Document;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

import java.util.Date;

public class WebDocumentSearchEntryMapper implements SearchEntryMapper<Document> {

	private boolean current;

	public WebDocumentSearchEntryMapper() {
		this(true);
	}

	public WebDocumentSearchEntryMapper(boolean current) {
		this.current = current;
	}

	@Override
	public SearchEntry map(Document document) {
		SearchEntry entry = new SearchEntry();
		entry.setPublishAt(new Date(0L));
		String text = document.text();
		entry.setRawContent(text);
		entry.setSummary(text.substring(0, Math.min(500, text.length())));
		entry.setTitle(document.title());
		entry.setPath(document.baseUri());
		entry.setCurrent(current);
		return entry;
	}

}
