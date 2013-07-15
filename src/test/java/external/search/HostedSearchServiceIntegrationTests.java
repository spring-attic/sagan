package external.search;

import org.jsoup.nodes.Document;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.bootstrap.context.initializer.LoggingApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.guides.GettingStartedService;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.indexer.WebDocumentSearchEntryMapper;
import org.springframework.site.indexer.configuration.IndexerConfiguration;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.guides.GuideSearchEntryMapper;
import org.springframework.site.web.search.SearchEntryBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IndexerConfiguration.class,
		initializers = {ConfigFileApplicationContextInitializer.class,
				        LoggingApplicationContextInitializer.class})
@ActiveProfiles("integration-test")
@Configuration
public class HostedSearchServiceIntegrationTests {

	public static final String SEARCH_ENDPOINT = "elasticsearch.client.endpoint";
	public static String originalSearchEndpoint;

	@BeforeClass
	public static void setUp() {
		originalSearchEndpoint = System.getProperty(SEARCH_ENDPOINT);
		System.setProperty(SEARCH_ENDPOINT, "https://qlmsjwfa.api.qbox.io");
	}

	@AfterClass
	public static void clear() {
		if (originalSearchEndpoint == null) {
			System.clearProperty(SEARCH_ENDPOINT);
		} else {
			System.setProperty(SEARCH_ENDPOINT, originalSearchEndpoint);
		}
	}

	private final Pageable pageable = new PageRequest(0,10);

	@Autowired
	private SearchService searchService;

	@Autowired
	private GettingStartedService gettingStartedService;

	private SearchEntry entry;

	// This test works in the "live" index, so the index is neither created nor deleted.

	@After
	public void tearDown() throws Exception {
		searchService.removeFromIndex(entry);
	}

	@Test
	public void simpleEntryIndexing() throws ParseException, InterruptedException {
		entry = SearchEntryBuilder.entry()
				.path("/blog/integration_test-1")
				.title("Integration Test Title")
				.rawContent("Integration test content")
				.summary("Integration summary")
				.publishAt("2013-01-01 10:00")
				.build();

		searchService.saveToIndex(entry);

		Thread.sleep(1000);

		Page<SearchEntry> searchEntries = searchService.search("Integration test content", pageable);
		List<SearchEntry> entries = searchEntries.getContent();
		assertThat(entries, not(empty()));
		assertThat(entries.get(0).getSummary(), is(equalTo(entry.getSummary())));
	}

	@Test
	public void gettingStarteGuideIndexing() throws ParseException, InterruptedException {
		List<GuideRepo> guideRepos = gettingStartedService.listGuides();
		GuideRepo firstGuide = guideRepos.get(0);
		GettingStartedGuide guide = gettingStartedService.loadGuide(firstGuide.getGuideId());

		GuideSearchEntryMapper guideEntryMapper = new GuideSearchEntryMapper();
		entry = guideEntryMapper.map(guide);

		entry.setPath(entry.getPath() + "gsg-integration-test");
		String testTitle = "Somerandomtitlealltogetherwhichiseasierforsearchduringtesting";
		entry.setTitle(testTitle);

		searchService.saveToIndex(entry);

		Thread.sleep(1000);

		Page<SearchEntry> searchEntries = searchService.search(testTitle, pageable);
		List<SearchEntry> entries = searchEntries.getContent();
		assertThat(entries, not(empty()));
		assertThat(entries.get(0).getTitle(), is(equalTo(entry.getTitle())));
	}

	@Test
	public void apiDocsIndexing() throws ParseException, InterruptedException {
		String apiDocsTestClassUrl = "http://static.springsource.org/spring/docs/3.1.4.RELEASE/javadoc-api/org/springframework/aop/framework/autoproxy/AbstractAdvisorAutoProxyCreator.html";

		Document testApiDocument = Document.createShell(apiDocsTestClassUrl);
		testApiDocument.body().text("Somereandomtestcontentthatshouldbeunique");
		testApiDocument.setBaseUri(apiDocsTestClassUrl);

		WebDocumentSearchEntryMapper mapper = new WebDocumentSearchEntryMapper();
		entry = mapper.map(testApiDocument);

		entry.setPath(entry.getPath() + "api-docs-indexing-integration-test");

		searchService.saveToIndex(entry);

		Thread.sleep(1000);

		Page<SearchEntry> searchEntries = searchService.search("Somereandomtestcontentthatshouldbeunique", pageable);
		List<SearchEntry> entries = searchEntries.getContent();
		assertThat(entries, not(empty()));
		SearchEntry entry = entries.get(0);
		assertThat(entry.getTitle(), is(equalTo(entry.getTitle())));
	}


}
