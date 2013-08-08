package org.springframework.site.indexer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.StaticPageMapper;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.search.SearchService;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;

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
	private StaticPageMapper staticPageMapper;

	private StaticPageIndexer indexer;

	@Before
	public void setUp() throws Exception {
		indexer = new StaticPageIndexer(crawlerService, searchService, staticPageMapper);
		ReflectionTestUtils.setField(indexer, "baseUrl", "http://www.example.com");
	}

	@Test
	public void itReturnsStaticPages() throws IOException {
		StaticPageMapper.StaticPageMapping mapping = new StaticPageMapper.StaticPageMapping("/template/path", "/foo");
		given(staticPageMapper.staticPagePaths()).willReturn(Arrays.asList(mapping));

		Iterable<String> pages = indexer.indexableItems();

		assertThat(pages, containsInAnyOrder("http://www.example.com/foo"));
	}

	@Test
	public void itIgnoresErrorPages() throws IOException {
		StaticPageMapper.StaticPageMapping mapping = new StaticPageMapper.StaticPageMapping("/template/path", "/foo");
		StaticPageMapper.StaticPageMapping error1 = new StaticPageMapper.StaticPageMapping("/template/path", "/error");
		StaticPageMapper.StaticPageMapping error2 = new StaticPageMapper.StaticPageMapping("/template/path", "/404");
		StaticPageMapper.StaticPageMapping error3 = new StaticPageMapper.StaticPageMapping("/template/path", "/500");
		given(staticPageMapper.staticPagePaths()).willReturn(Arrays.asList(mapping, error1, error2, error3));

		Iterable<String> pages = indexer.indexableItems();

		assertThat(pages, containsInAnyOrder("http://www.example.com/foo"));
	}
}
