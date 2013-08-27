package integration.indexer;

import integration.IntegrationTestBase;
import io.spring.site.domain.StaticPagePathFinder;
import io.spring.site.domain.guides.Guide;
import io.spring.site.domain.guides.GuidesService;
import io.spring.site.indexer.GettingStartedGuideIndexer;
import io.spring.site.indexer.StaticPageIndexer;
import io.spring.site.indexer.ToolsIndexer;
import io.spring.site.indexer.TutorialIndexer;
import io.spring.site.indexer.crawler.CrawlerService;
import io.spring.site.indexer.crawler.DocumentProcessor;
import io.spring.site.search.SearchEntry;
import io.spring.site.search.SearchService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class IndexerTests extends IntegrationTestBase {

	private CrawlerService stubCrawlerService = new CrawlerService(
	) {
		@Override
		public void crawl(String url, int linkDepth, DocumentProcessor processor) {
			try {
				MvcResult response = IndexerTests.this.mockMvc.perform(get(url))
						.andReturn();
				Document html = Jsoup.parse(response.getResponse().getContentAsString());
				processor.process(html);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	};

	public SearchEntry indexedEntry;

	private SearchService stubSearchService = new SearchService(null, null) {
		@Override
		public void saveToIndex(SearchEntry entry) {
			IndexerTests.this.indexedEntry = entry;
		}
	};

	@Autowired
	private RestTemplate restTemplate;

	@Before
	public void setup() throws IOException {
		InputStream response = new ClassPathResource("/sts_downloads.xml", getClass())
				.getInputStream();
		String responseXml = StreamUtils.copyToString(response, Charset.forName("UTF-8"));

		stub(this.restTemplate.getForObject(anyString(), eq(String.class))).toReturn(
				responseXml);
	}

	@Test
	public void toolPagesAreNotIndexedWithHeaderOrFooterContent() throws Exception {
		ToolsIndexer toolsIndexer = new ToolsIndexer(this.stubCrawlerService,
				this.stubSearchService);

		toolsIndexer.indexItem("/tools");

		assertThat(this.indexedEntry.getRawContent(), containsString("Tools"));

		assertThat(this.indexedEntry.getRawContent(), not(containsString("<div>")));
		assertThat(this.indexedEntry.getRawContent(), not(containsString("Blog")));
		assertThat(this.indexedEntry.getRawContent(),
				not(containsString("Documentation")));
		assertThat(this.indexedEntry.getRawContent(), not(containsString("Privacy")));
		assertThat(this.indexedEntry.getRawContent(),
				not(containsString("Trademark Standards")));
		assertThat(this.indexedEntry.getRawContent(), not(containsString("Terms of Use")));
	}

	@Test
	public void staticPagesAreNotIndexedWithHeaderOrFooterContent() throws Exception {
		StaticPageIndexer staticPageIndexer = new StaticPageIndexer(
				this.stubCrawlerService, this.stubSearchService,
				mock(StaticPagePathFinder.class));

		staticPageIndexer.indexItem("/about");

		assertThat(this.indexedEntry.getRawContent(), containsString("About"));

		assertThat(this.indexedEntry.getRawContent(), not(containsString("<div>")));
		assertThat(this.indexedEntry.getRawContent(), not(containsString("Blog")));
		assertThat(this.indexedEntry.getRawContent(),
				not(containsString("Documentation")));
		assertThat(this.indexedEntry.getRawContent(), not(containsString("Tools")));
		assertThat(this.indexedEntry.getRawContent(), not(containsString("Privacy")));
		assertThat(this.indexedEntry.getRawContent(),
				not(containsString("Trademark Standards")));
		assertThat(this.indexedEntry.getRawContent(), not(containsString("Terms of Use")));
	}

	@Test
	public void tutorialGuidesAreIndexed() throws Exception {
		TutorialIndexer tutorialIndexer = new TutorialIndexer(this.stubSearchService,
				mock(GuidesService.class));

		Guide restTutorial = new Guide("tut-rest", "rest", "Learn rest", "rest subtitle",
				"This is the rest tutorial content", "");
		tutorialIndexer.indexItem(restTutorial);

		assertThat(this.indexedEntry.getRawContent(),
				equalTo("This is the rest tutorial content"));
		assertThat(this.indexedEntry.getFacetPaths(),
				containsInAnyOrder("Guides", "Guides/Tutorials"));
		assertThat(this.indexedEntry.getTitle(), equalTo("Learn rest"));
		assertThat(this.indexedEntry.getSubTitle(), equalTo("Tutorial"));
		assertThat(this.indexedEntry.getSummary(),
				equalTo("This is the rest tutorial content"));
		assertThat(this.indexedEntry.getPublishAt(), equalTo(new Date(0L)));
	}

	@Test
	public void gettingStartedGuidesAreIndexed() throws Exception {
		GettingStartedGuideIndexer tutorialIndexer = new GettingStartedGuideIndexer(
				this.stubSearchService, mock(GuidesService.class));

		Guide restTutorial = new Guide("gs-rest-service", "rest-service",
				"Learn about rest", "rest subtitle", "This is the rest guide content",
				"This is the sidebar");
		tutorialIndexer.indexItem(restTutorial);

		assertThat(this.indexedEntry.getRawContent(),
				equalTo("This is the rest guide content"));
		assertThat(this.indexedEntry.getFacetPaths(),
				containsInAnyOrder("Guides", "Guides/Getting Started"));
		assertThat(this.indexedEntry.getTitle(), equalTo("Learn about rest"));
		assertThat(this.indexedEntry.getSubTitle(), equalTo("Getting Started Guide"));
		assertThat(this.indexedEntry.getSummary(),
				equalTo("This is the rest guide content"));
		assertThat(this.indexedEntry.getPublishAt(), equalTo(new Date(0L)));
	}
}
