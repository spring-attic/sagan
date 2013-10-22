package examples.indexer;

import sagan.app.indexer.ApplicationConfiguration;
import sagan.guides.Guide;
import sagan.guides.UnderstandingGuide;
import sagan.guides.search.GettingStartedGuideIndexer;
import sagan.guides.search.UnderstandingGuideIndexer;
import sagan.search.SearchIndexSetup;
import sagan.search.SearchResult;
import sagan.search.SearchResults;
import sagan.search.service.SearchService;
import sagan.util.LongRunning;
import sagan.util.SetSystemProperty;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.boot.context.initializer.LoggingApplicationContextInitializer;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Iterables;

import io.searchbox.client.JestClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfiguration.class }, initializers = {
        ConfigFileApplicationContextInitializer.class, LoggingApplicationContextInitializer.class })
@DirtiesContext
public class IndexerExamples {

    @ClassRule
    public static LongRunning longRunning = LongRunning.create();

    @ClassRule
    public static SetSystemProperty delay = new SetSystemProperty("search.indexer.delay", "60000000");

    @Autowired
    private GettingStartedGuideIndexer gettingStartedGuideIndexer;

    @Autowired
    private UnderstandingGuideIndexer understandingGuideIndexer;

    @Autowired
    private SearchService searchService;

    @Autowired
    private JestClient jestClient;

    @Value("${elasticsearch.client.index}")
    private String index;

    @Before
    public void setUp() throws Exception {
        rebuildIndex();
    }

    @After
    public void tearDown() throws Exception {
        rebuildIndex();
    }

    @Test
    public void gettingStartedGuideIndexer_indexableItems() {
        Iterable<Guide> items = this.gettingStartedGuideIndexer.indexableItems();
        assertThat(Iterables.size(items), greaterThan(0));
    }

    @Test
    public void gettingStartedGuideIndexer_indexesAnItem() {
        Guide gettingStartedGuide =
                new Guide("gs-rest-service", "rest-service", "title", "subtitle", "rest", "This is the sidebar!");
        this.gettingStartedGuideIndexer.indexItem(gettingStartedGuide);

        SearchResults searchResults = this.searchService.search("rest", new PageRequest(0, 10), null);
        assertThat(searchResults.getPage().getContent().size(), is(1));
    }

    @Test
    public void understandingGuideIndexer_indexableItems() {
        Iterable<UnderstandingGuide> items = this.understandingGuideIndexer.indexableItems();
        assertThat(Iterables.size(items), greaterThan(8));
    }

    @Test
    public void understandingGuideIndexer_indexesAnItem() throws Exception {
        UnderstandingGuide guide =
                new UnderstandingGuide("foo", "<h1>Understanding: foo</h1><p>content</p>", "<p>sidebar</p>");
        this.understandingGuideIndexer.indexItem(guide);

        SearchResults searchResults = this.searchService.search("foo", new PageRequest(0, 10), null);
        List<SearchResult> results = searchResults.getPage().getContent();
        assertThat(results.size(), is(1));
    }

    private void rebuildIndex() {
        SearchIndexSetup searchIndexSetup = new SearchIndexSetup(this.jestClient, this.index);
        searchIndexSetup.deleteIndex();
        searchIndexSetup.createIndex();
    }
}
