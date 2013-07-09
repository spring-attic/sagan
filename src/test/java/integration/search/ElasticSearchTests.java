package integration.search;

import io.searchbox.Parameters;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.bootstrap.context.initializer.LoggingApplicationContextInitializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.search.SearchEntry;
import org.springframework.search.configuration.InMemoryElasticSearchConfiguration;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.search.SearchEntryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Ignore("Useful test for validating elasticsearch API")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfiguration.class, InMemoryElasticSearchConfiguration.class }, initializers = {
		ConfigFileApplicationContextInitializer.class,
		LoggingApplicationContextInitializer.class })
public class ElasticSearchTests {

	@Autowired
	private ElasticsearchOperations elasticsearchTemplate;

	@Autowired
	private JestClient jestClient;

	@Before
	public void setUp() throws Exception {
		this.elasticsearchTemplate.deleteIndex(SearchEntry.class);
	}

	@Test
	public void testJestIndex() throws Exception {
		SearchEntry entry = saveJestEntryToElasticSearch();
		String query = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"site.rawContent\":\"raw\"}}]}},\"from\":0,\"size\":10}";
		JestResult result = this.jestClient.execute(new Search(query));

		List<SearchEntry> list = result.<List<SearchEntry>> getSourceAsObjectList(SearchEntry.class);

		Page<SearchEntry> results = new PageImpl<SearchEntry>(list);
		assertThat(results.getContent().get(0).getRawContent(),is(equalTo(entry.getRawContent())));
	}

	private SearchEntry saveJestEntryToElasticSearch() throws Exception {
		SearchEntry entry = SearchEntryBuilder.entry()
				.title("This week in Spring")
				.rawContent("This is some raw content").build();

		Index index = new Index.Builder(entry)
				.id("2")
				.index("site")
				.type("site")
				.build();
		index.addParameter(Parameters.REFRESH, true);
		JestResult result = this.jestClient.execute(index);

		System.out.println(result.getJsonString());
		return entry;
	}

}
