package sagan.staticpage.support;

import sagan.search.support.CrawlerService;
import sagan.search.support.SearchService;
import sagan.support.StaticPagePathFinder;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StaticPageIndexerTests {

    @Mock
    private CrawlerService crawlerService;

    @Mock
    private SearchService searchService;

    @Mock
    private StaticPagePathFinder staticPagePathFinder;

    private StaticPageIndexer indexer;

    @Before
    public void setUp() throws Exception {
        indexer = new StaticPageIndexer(crawlerService, searchService, staticPagePathFinder);
        ReflectionTestUtils.setField(indexer, "baseUrl", "http://www.example.com");
    }

    @Test
    public void itReturnsStaticPages() throws IOException {
        StaticPagePathFinder.PagePaths paths = new StaticPagePathFinder.PagePaths("/template/path", "/foo");
        given(staticPagePathFinder.findPaths()).willReturn(Arrays.asList(paths));

        Iterable<String> pages = indexer.indexableItems();

        assertThat(pages, containsInAnyOrder("http://www.example.com/foo"));
    }

    @Test
    public void itIgnoresErrorPages() throws IOException {
        StaticPagePathFinder.PagePaths paths = new StaticPagePathFinder.PagePaths("/template/path", "/foo");
        StaticPagePathFinder.PagePaths error1 = new StaticPagePathFinder.PagePaths("/template/path", "/error");
        StaticPagePathFinder.PagePaths error2 = new StaticPagePathFinder.PagePaths("/template/path", "/404");
        StaticPagePathFinder.PagePaths error3 = new StaticPagePathFinder.PagePaths("/template/path", "/500");
        given(staticPagePathFinder.findPaths()).willReturn(Arrays.asList(paths, error1, error2, error3));

        Iterable<String> pages = indexer.indexableItems();

        assertThat(pages, containsInAnyOrder("http://www.example.com/foo"));
    }
}
