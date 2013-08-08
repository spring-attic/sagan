package integration.indexer;

import integration.IntegrationTestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.StaticPageMapper;
import org.springframework.site.indexer.StaticPageIndexer;
import org.springframework.site.indexer.ToolsIndexer;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.indexer.crawler.DocumentProcessor;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class IndexerTests extends IntegrationTestBase {

	private CrawlerService stubCrawlerService = new CrawlerService() {
		@Override
		public void crawl(String url, int linkDepth, DocumentProcessor processor) {
			try {
				MvcResult response = mockMvc.perform(get(url)).andReturn();
				Document html = Jsoup.parse(response.getResponse().getContentAsString());
				processor.process(html);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	};

	public SearchEntry indexedEntry;

	private SearchService stubSearchService = new SearchService(null, null){
		@Override
		public void saveToIndex(SearchEntry entry) {
			indexedEntry = entry;
		}
	};

	@Autowired
	private RestTemplate restTemplate;

	@Before
	public void setup() throws IOException {
		InputStream response = new ClassPathResource("/sts_downloads.xml", getClass()).getInputStream();
		String responseXml = StreamUtils.copyToString(response, Charset.forName("UTF-8"));

		stub(restTemplate.getForObject(anyString(), eq(String.class))).toReturn(responseXml);
	}

	@Test
	public void toolPagesAreNotIndexedWithHeaderOrFooterContent() throws Exception {
		ToolsIndexer toolsIndexer = new ToolsIndexer(stubCrawlerService, stubSearchService);

		toolsIndexer.indexItem("/tools");

		assertThat(indexedEntry.getRawContent(), containsString("Tools"));

		assertThat(indexedEntry.getRawContent(), not(containsString("<div>")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Blog")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Documentation")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Privacy")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Trademark Standards")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Terms of Use")));
	}

	@Test
	public void staticPagesAreNotIndexedWithHeaderOrFooterContent() throws Exception {
		StaticPageIndexer staticPageIndexer = new StaticPageIndexer(stubCrawlerService, stubSearchService, mock(StaticPageMapper.class));

		staticPageIndexer.indexItem("/about");

		assertThat(indexedEntry.getRawContent(), containsString("About"));

		assertThat(indexedEntry.getRawContent(), not(containsString("<div>")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Blog")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Documentation")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Tools")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Privacy")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Trademark Standards")));
		assertThat(indexedEntry.getRawContent(), not(containsString("Terms of Use")));
	}
}
