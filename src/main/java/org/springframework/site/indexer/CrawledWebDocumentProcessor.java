package org.springframework.site.indexer;

import org.jsoup.nodes.Document;
import org.springframework.site.indexer.crawler.DocumentProcessor;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;
import org.springframework.site.search.SearchService;

public class CrawledWebDocumentProcessor implements DocumentProcessor {
	private final SearchService searchService;
	private final SearchEntryMapper<Document> mapper;

	public CrawledWebDocumentProcessor(SearchService searchService, SearchEntryMapper<Document> documentMapper) {
		this.searchService = searchService;
		this.mapper = documentMapper;
	}

	@Override
	public void process(Document document) {
		SearchEntry searchEntry = mapper.map(document);
		if (searchEntry != null) {
			searchService.saveToIndex(searchEntry);
		}
	}
}
