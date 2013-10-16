package examples.search;

import io.spring.site.domain.guides.Guide;
import io.spring.site.domain.guides.GuidesService;
import io.spring.site.indexer.configuration.IndexerConfiguration;
import io.spring.site.indexer.mapper.GuideSearchEntryMapper;
import io.spring.site.indexer.mapper.WebDocumentSearchEntryMapper;
import io.spring.site.search.SearchEntry;
import io.spring.site.search.SearchResult;
import io.spring.site.search.SearchService;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.boot.context.initializer.LoggingApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import utils.LongRunning;
import utils.SearchEntryBuilder;
import utils.SetSystemProperty;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IndexerConfiguration.class, initializers = {
        ConfigFileApplicationContextInitializer.class,
        LoggingApplicationContextInitializer.class })
@ActiveProfiles("integration-test")
@Configuration
@DirtiesContext
public class HostedSearchServiceIntegrationExamples {

    @ClassRule
    public static LongRunning longRunning = LongRunning.create();

    @ClassRule
    public static SetSystemProperty elasticSearchEndpoint = new SetSystemProperty(
            "elasticsearch.client.endpoint", "https://qlmsjwfa.api.qbox.io");

    @ClassRule
    public static SetSystemProperty searchIndexerDelay = new SetSystemProperty(
            "search.indexer.delay", "60000000");

    private final Pageable pageable = new PageRequest(0, 10);

    @Autowired
    private SearchService searchService;

    @Autowired
    private GuidesService guidesService;

    private SearchEntry entry;

    // This test works in the "live" index, so the index is neither created nor deleted.

    @After
    public void tearDown() throws Exception {
        this.searchService.removeFromIndex(this.entry);
    }

    @Test
    public void simpleEntryIndexing() throws ParseException, InterruptedException {
        this.entry = SearchEntryBuilder.entry().path("/blog/integration_test-1")
                .title("Integration Test Title").rawContent("Integration test content")
                .summary("Integration summary").publishAt("2013-01-01 10:00").build();

        this.searchService.saveToIndex(this.entry);

        Thread.sleep(1000);

        Page<SearchResult> searchEntries = this.searchService.search(
                "Integration test content", this.pageable,
                Collections.<String> emptyList()).getPage();
        List<SearchResult> entries = searchEntries.getContent();
        assertThat(entries, not(empty()));
        assertThat(entries.get(0).getSummary(), is(equalTo(this.entry.getSummary())));
    }

    @Test
    public void gettingStartedGuideIndexing() throws ParseException, InterruptedException {
        List<Guide> guideShells = this.guidesService.listGettingStartedGuides();
        Guide guideShell = guideShells.get(0);
        Guide guide = this.guidesService.loadGettingStartedGuide(guideShell.getGuideId());

        GuideSearchEntryMapper guideEntryMapper = new GuideSearchEntryMapper();
        this.entry = guideEntryMapper.map(guide);

        this.entry.setPath(this.entry.getPath() + "gsg-integration-test");
        String testTitle = "Somerandomtitlealltogetherwhichiseasierforsearchduringtesting";
        this.entry.setTitle(testTitle);

        this.searchService.saveToIndex(this.entry);

        Thread.sleep(1000);

        Page<SearchResult> searchEntries = this.searchService.search(testTitle,
                this.pageable, Collections.<String> emptyList()).getPage();
        List<SearchResult> entries = searchEntries.getContent();
        assertThat(entries, not(empty()));
        assertThat(entries.get(0).getTitle(), is(equalTo(this.entry.getTitle())));
    }

    @Test
    public void apiDocsIndexing() throws ParseException, InterruptedException {
        String apiDocsTestClassUrl = "http://docs.spring.io/spring/docs/3.1.4.RELEASE/javadoc-api/org/springframework/aop/framework/autoproxy/AbstractAdvisorAutoProxyCreator.html";

        Document testApiDocument = Document.createShell(apiDocsTestClassUrl);
        testApiDocument.body().text("Somereandomtestcontentthatshouldbeunique");
        testApiDocument.setBaseUri(apiDocsTestClassUrl);

        WebDocumentSearchEntryMapper mapper = new WebDocumentSearchEntryMapper();
        this.entry = mapper.map(testApiDocument);

        this.entry.setPath(this.entry.getPath() + "api-docs-indexing-integration-test");

        this.searchService.saveToIndex(this.entry);

        Thread.sleep(1000);

        Page<SearchResult> searchEntries = this.searchService.search(
                "Somereandomtestcontentthatshouldbeunique", this.pageable,
                Collections.<String> emptyList()).getPage();
        List<SearchResult> entries = searchEntries.getContent();
        assertThat(entries, not(empty()));
        SearchResult entry = entries.get(0);
        assertThat(entry.getTitle(), is(equalTo(entry.getTitle())));
    }

}
