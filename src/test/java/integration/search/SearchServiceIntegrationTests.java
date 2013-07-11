package integration.search;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.bootstrap.context.initializer.LoggingApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.configuration.ApplicationConfiguration;
import org.springframework.site.web.search.SearchEntryBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import utils.FreePortFinder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfiguration.class, SearchServiceIntegrationTests.IntegrationTestElasticSearchConfiguration.class},
		initializers = {ConfigFileApplicationContextInitializer.class, LoggingApplicationContextInitializer.class})
@DirtiesContext
public class SearchServiceIntegrationTests {

	public static class IntegrationTestElasticSearchConfiguration {

		@Autowired
		private SearchService searchService;

		@Autowired
		private Client client;

		private String elasticSearchPort;

		@Bean
		public Client elasticSearchClient() throws Exception {
			NodeBuilder nodeBuilder = nodeBuilder().local(false);
			nodeBuilder.getSettings().put("network.host", "127.0.0.1");
			nodeBuilder.getSettings().put("http.port", getElasticSearchPort());
			nodeBuilder.getSettings().put("index.number_of_shards", "1");
			nodeBuilder.getSettings().put("index.number_of_replicas", "0");
			Client client = nodeBuilder.node().client();
			return client;
		}

		private String getElasticSearchPort() {
			if (elasticSearchPort == null) {
				elasticSearchPort = FreePortFinder.find() + "";
			}
			return elasticSearchPort;
		}

		@PostConstruct
		public void configureSearchService() {
			searchService.setUseRefresh(true);
		}

		@PreDestroy
		public void closeClient() throws Exception {
			client.close();
		}

		@Bean
		@Primary
		public JestClient jestClient() {
			JestClientFactory factory = new JestClientFactory();
			factory.setClientConfig(clientConfig());
			return factory.getObject();
		}

		private ClientConfig clientConfig() {
			ClientConfig clientConfig = new ClientConfig();
			LinkedHashSet<String> servers = new LinkedHashSet<String>();
			servers.add("http://localhost:" + getElasticSearchPort());
			clientConfig.getProperties().put(ClientConstants.SERVER_LIST, servers);
			clientConfig.getProperties().put(ClientConstants.IS_MULTI_THREADED, true);
			return clientConfig;
		}
	}

	private final Pageable pageable = new PageRequest(0,10);

	@Autowired
	private SearchService searchService;

	private SearchEntry entry;

	@Before
	public void setUp() throws Exception {
		searchService.deleteIndex();
		searchService.createIndex();
	}

	private void indexSingleEntry() throws ParseException {
		entry = createSingleEntry("/some/path");
		searchService.saveToIndex(entry);
	}

	private SearchEntry createSingleEntry(String path) throws ParseException {
		return SearchEntryBuilder.entry()
				.path(path)
				.title("This week in Spring")
				.rawContent("raw content")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.build();
	}

	private void assertThatSearchReturnsEntry(String query) {
		Page<SearchEntry> searchEntries = searchService.search(query, pageable);
		List<SearchEntry> entries = searchEntries.getContent();
		assertThat(entries, not(empty()));
		assertThat(entries.get(0).getSummary(), is(equalTo(entry.getSummary())));
	}

	@Test
	public void testSearchContent() throws ParseException {
		indexSingleEntry();
		assertThatSearchReturnsEntry("raw");
	}

	@Test
	public void testSearchTitle() throws ParseException {
		indexSingleEntry();
		assertThatSearchReturnsEntry("Spring");
	}

	@Test
	public void testSearchWithMultipleWords() throws ParseException {
		indexSingleEntry();
		assertThatSearchReturnsEntry("raw content");
	}

	@Test
	public void searchOnlyIncludesEntriesMatchingSearchTerm() throws ParseException {
		indexSingleEntry();

		SearchEntry secondEntry = SearchEntryBuilder.entry()
				.path("/another/path")
				.title("Test")
				.rawContent("Test body")
				.build();

		searchService.saveToIndex(secondEntry);
		Page<SearchEntry> searchEntries = searchService.search("content", pageable);
		List<SearchEntry> entries = searchEntries.getContent();
		assertThat(entries.size(), equalTo(1));
	}

	@Test
	public void searchPagesProperly() throws ParseException {
		SearchEntryBuilder builder  = SearchEntryBuilder.entry()
				.rawContent("raw content")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00");

		SearchEntry entry1 = builder.path("item1").title("Item 1").build();
		searchService.saveToIndex(entry1);

		SearchEntry entry2 = builder.path("item2").title("Item 2").build();
		searchService.saveToIndex(entry2);

		Pageable page1 = new PageRequest(0,1);
		Page<SearchEntry> searchEntries1 = searchService.search("content", page1);
		assertThat(searchEntries1.getContent().get(0).getId(), equalTo(entry1.getId()));

		Pageable page2 = new PageRequest(1,1);
		Page<SearchEntry> searchEntries2 = searchService.search("content", page2);
		assertThat(searchEntries2.getContent().get(0).getId(), equalTo(entry2.getId()));
	}
}
