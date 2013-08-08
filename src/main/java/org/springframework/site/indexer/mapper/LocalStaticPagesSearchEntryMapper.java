package org.springframework.site.indexer.mapper;

import org.jsoup.nodes.Document;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

import java.util.Date;

public class LocalStaticPagesSearchEntryMapper implements SearchEntryMapper<Document> {

	@Override
	public SearchEntry map(Document document) {
		SearchEntry entry = new SearchEntry();
		entry.setPublishAt(new Date(0L));
		document.getElementsByTag("header").remove();
		document.getElementsByTag("footer").remove();
		String text = document.text();
		entry.setRawContent(text);
		entry.setSummary(text.substring(0, Math.min(500, text.length())));
		entry.setTitle(document.title());
		entry.setPath(document.baseUri());
		return entry;
	}

}
