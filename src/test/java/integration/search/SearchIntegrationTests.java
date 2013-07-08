package integration.search;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.search.configuration.InMemoryElasticSearchConfiguration;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.search.SearchController;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.ExtendedModelMap;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfiguration.class, InMemoryElasticSearchConfiguration.class}, initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("local")
public class SearchIntegrationTests {

	@Autowired
	private ElasticsearchOperations elasticsearchTemplate;

	@Autowired
	private SearchController searchController;

	private SearchEntry entry;
	private ExtendedModelMap model = new ExtendedModelMap();


	@Autowired
	private Environment env;

	@Before
	public void setUp() throws Exception {
		elasticsearchTemplate.deleteIndex(SearchEntry.class);
		entry = createSingleEntry();
		addEntryToIndex(entry, "1");
	}

	@Test
	public void testSearchContent() {
		String[] activeProfiles = env.getActiveProfiles();

		searchController.search("raw", 1, model);

		List<SearchEntry> entries = (List<SearchEntry>) model.get("results");
		assertThat(entries, not(empty()));
		assertThat(entries.get(0).getSummary(), is(equalTo(entry.getSummary())));
	}

	@Test
	public void testSearchTitle() {
		searchController.search("Spring", 1, model);

		List<SearchEntry> entries = (List<SearchEntry>) model.get("results");
		assertThat(entries, not(empty()));
		assertThat(entries.get(0).getSummary(), is(equalTo(entry.getSummary())));
	}

	@Test
	public void testSearchWithMultipleWords() {
		searchController.search("raw content", 1, model);

		List<SearchEntry> entries = (List<SearchEntry>) model.get("results");
		assertThat(entries, not(empty()));
		assertThat(entries.get(0).getSummary(), is(equalTo(entry.getSummary())));
	}

	@Test
	public void searchOnlyIncludesEntriesMatchingSearchTerm() throws ParseException {
		SearchEntry secondEntry = SearchEntryBuilder.entry()
				.id(2L)
				.title("Test")
				.rawContent("Test body")
				.build();

		addEntryToIndex(secondEntry, "2");

		searchController.search("content", 1, model);

		List<SearchEntry> entries = (List<SearchEntry>) model.get("results");
		assertThat(entries.size(), equalTo(1));
	}

	private void addEntryToIndex(SearchEntry searchEntry, String id) {
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId(id);
		indexQuery.setObject(searchEntry);
		elasticsearchTemplate.index(indexQuery);
		elasticsearchTemplate.refresh(SearchEntry.class, true);
	}

	private SearchEntry createSingleEntry() throws ParseException {
		SearchEntry entry = SearchEntryBuilder.entry()
				.title("This week in Spring")
				.rawContent("raw content")
				.summary("Html summary")
				.publishAt("2013-01-01 10:00")
				.build();
		return entry;
	}

}
