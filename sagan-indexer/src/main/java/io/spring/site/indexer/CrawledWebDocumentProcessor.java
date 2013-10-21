package io.spring.site.indexer;

import io.spring.site.indexer.crawler.DocumentProcessor;
import sagan.search.SearchEntry;
import sagan.search.SearchEntryMapper;
import sagan.search.service.SearchService;

import org.jsoup.nodes.Document;

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
