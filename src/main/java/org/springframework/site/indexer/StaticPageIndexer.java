package org.springframework.site.indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.site.domain.StaticPageMapper;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StaticPageIndexer implements Indexer<String> {

	@Value(value = "${search.indexer.base_url:http://localhost:8080}")
	private String baseUrl;

	private final CrawlerService crawlerService;
	private final StaticPageMapper staticPageMapper;
	private final CrawledWebDocumentProcessor documentProcessor;

	@Autowired
	public StaticPageIndexer(CrawlerService crawlerService, SearchService searchService, StaticPageMapper staticPageMapper) {
		this.crawlerService = crawlerService;
		this.staticPageMapper = staticPageMapper;
		this.documentProcessor = new CrawledWebDocumentProcessor(searchService, new WebDocumentSearchEntryMapper());
	}

	@Override
	public Iterable<String> indexableItems() {
		List<String> paths = new ArrayList<>();
		try {
			for (StaticPageMapper.StaticPageMapping staticPageMapping : staticPageMapper.staticPagePaths()) {
				paths.add(baseUrl + staticPageMapping.getUrlPath());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return paths;
	}

	@Override
	public void indexItem(String path) {
		crawlerService.crawl(path, 0, documentProcessor);
	}

	@Override
	public String counterName() {
		return "static";
	}

	@Override
	public String getId(String path) {
		return path;
	}
}
