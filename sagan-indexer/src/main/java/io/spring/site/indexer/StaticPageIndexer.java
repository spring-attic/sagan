package io.spring.site.indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sagan.util.web.StaticPagePathFinder;
import io.spring.site.indexer.crawler.CrawlerService;
import io.spring.site.indexer.mapper.LocalStaticPagesSearchEntryMapper;
import sagan.search.service.SearchService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StaticPageIndexer implements Indexer<String> {

    @Value(value = "${search.indexer.base_url:http://localhost:8080}")
    private String baseUrl;

    private final CrawlerService crawlerService;
    private final StaticPagePathFinder staticPagePathFinder;
    private final CrawledWebDocumentProcessor documentProcessor;

    private static final Set<String> pagesToIgnore = new HashSet<>();
    static {
        pagesToIgnore.add("/error");
        pagesToIgnore.add("/500");
        pagesToIgnore.add("/404");
    }

    @Autowired
    public StaticPageIndexer(CrawlerService crawlerService, SearchService searchService, StaticPagePathFinder staticPagePathFinder) {
        this.crawlerService = crawlerService;
        this.staticPagePathFinder = staticPagePathFinder;
        this.documentProcessor = new CrawledWebDocumentProcessor(searchService, new LocalStaticPagesSearchEntryMapper());
    }

    @Override
    public Iterable<String> indexableItems() {
        List<String> paths = new ArrayList<>();
        try {
            for (StaticPagePathFinder.PagePaths pagePaths : staticPagePathFinder.findPaths()) {
                if (!pagesToIgnore.contains(pagePaths.getUrlPath())) {
                    paths.add(baseUrl + pagePaths.getUrlPath());
                }
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
