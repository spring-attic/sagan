package examples.indexer;


import com.google.common.collect.Iterables;
import integration.search.SearchIndexSetup;
import integration.search.SearchServiceIntegrationTestLocal;
import io.searchbox.client.JestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.boot.context.initializer.LoggingApplicationContextInitializer;
import org.springframework.data.domain.PageRequest;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.understanding.UnderstandingGuide;
import org.springframework.site.indexer.GettingStartedGuideIndexer;
import org.springframework.site.indexer.UnderstandingGuideIndexer;
import org.springframework.site.indexer.configuration.IndexerConfiguration;
import org.springframework.site.search.SearchResults;
import org.springframework.site.search.SearchService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import utils.SetSystemProperty;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {IndexerConfiguration.class,
								 SearchServiceIntegrationTestLocal.IntegrationTestElasticSearchConfiguration.class },
					  initializers = {ConfigFileApplicationContextInitializer.class,
									  LoggingApplicationContextInitializer.class })
public class IndexerExamples {

	@ClassRule
	public static SetSystemProperty systemProperty = new SetSystemProperty("search.indexer.delay", "60000000");

	@Autowired
	private GettingStartedGuideIndexer gettingStartedGuideIndexer;

	@Autowired
	private UnderstandingGuideIndexer understandingGuideIndexer;

	@Autowired
	private SearchService searchService;

	@Autowired
	private JestClient jestClient;

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
		Iterable<GettingStartedGuide> items = gettingStartedGuideIndexer.indexableItems();
		assertThat(Iterables.size(items), greaterThan(0));
	}

	@Test
	public void gettingStartedGuideIndexer_indexesAnItem() {
		GettingStartedGuide gettingStartedGuide = new GettingStartedGuide("gs-rest-service", "rest-service", "asdf", "asdf", "This is the sidebar!");
		gettingStartedGuideIndexer.indexItem(gettingStartedGuide);

		SearchResults searchResults = searchService.search("rest", new PageRequest(0, 10), null);
		assertThat(searchResults.getPage().getContent().size(), is(1));
	}

	@Ignore
	public void understandingGuideIndexer_indexesItems() {
		Iterable<UnderstandingGuide> items = understandingGuideIndexer.indexableItems();
		assertThat(Iterables.size(items), equalTo(9));
	}

	private void rebuildIndex() {
		SearchIndexSetup searchIndexSetup = new SearchIndexSetup(jestClient);
		searchIndexSetup.deleteIndex();
		searchIndexSetup.createIndex();
	}
}
