package sagan.staticpage.support;

import sagan.Indexer;
import sagan.search.support.CrawledWebDocumentProcessor;
import sagan.search.support.CrawlerService;
import sagan.search.support.SearchService;
import sagan.support.StaticPagePathFinder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public StaticPageIndexer(CrawlerService crawlerService, SearchService searchService,
                             StaticPagePathFinder staticPagePathFinder) {
        this.crawlerService = crawlerService;
        this.staticPagePathFinder = staticPagePathFinder;
        documentProcessor =
                new CrawledWebDocumentProcessor(searchService, new LocalStaticPagesSearchEntryMapper());
    }

    @Override
    public Iterable<String> indexableItems() {
        try {
            return staticPagePathFinder.findPaths().stream()
                    .filter(pagePaths -> !pagesToIgnore.contains(pagePaths.getUrlPath()))
                    .map(pagePaths -> baseUrl + pagePaths.getUrlPath())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
