package integration.search;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.search.SearchEntry;
import org.springframework.search.SearchService;
import org.springframework.search.configuration.InMemoryElasticSearchConfiguration;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.search.SearchEntryBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfiguration.class, InMemoryElasticSearchConfiguration.class},
		initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("local")
public class SearchServiceIntegrationTests {

	private final Pageable pageable = new PageRequest(0,10);

	@Autowired
	private SearchService searchService;

	private SearchEntry entry;

	@Before
	public void setUp() throws Exception {
		searchService.deleteIndex();
		entry = createSingleEntry("1");
		searchService.saveToIndex(entry);
	}

	private SearchEntry createSingleEntry(String id) throws ParseException {
		return SearchEntryBuilder.entry()
				.id(id)
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
	public void testSearchContent() {
		assertThatSearchReturnsEntry("raw");
	}

	@Test
	public void testSearchTitle() {
		assertThatSearchReturnsEntry("Spring");
	}

	@Test
	public void testSearchWithMultipleWords() {
		assertThatSearchReturnsEntry("raw content");
	}

	@Test
	public void searchOnlyIncludesEntriesMatchingSearchTerm() throws ParseException {
		SearchEntry secondEntry = SearchEntryBuilder.entry()
				.id("2")
				.title("Test")
				.rawContent("Test body")
				.build();

		searchService.saveToIndex(secondEntry);
		Page<SearchEntry> searchEntries = searchService.search("content", pageable);
		List<SearchEntry> entries = searchEntries.getContent();;
		assertThat(entries.size(), equalTo(1));
	}

}
