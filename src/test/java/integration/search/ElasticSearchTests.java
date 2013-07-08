package integration.search;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.FilterBuilders;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.core.env.Environment;
import org.springframework.bootstrap.context.initializer.LoggingApplicationContextInitializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.search.configuration.InMemoryElasticSearchConfiguration;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Ignore("Useful test for validating elasticsearch API")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfiguration.class, JestConfiguration.class, InMemoryElasticSearchConfiguration.class }, initializers = {
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
	public void testIndex() {
		SearchEntry result = saveSearchEntryToElasticSearch();

		CriteriaQuery criteriaQuery = new CriteriaQuery(
				new Criteria("rawContent").contains("raw"));

		this.elasticsearchTemplate.refresh(SearchEntry.class, true);

		Page<SearchEntry> results = this.elasticsearchTemplate.queryForPage(
				criteriaQuery, SearchEntry.class);
		assertThat(results.getContent().get(0).getRawContent(),
				is(equalTo(result.getRawContent())));
	}

	@Test
	public void testJestIndex() throws Exception {
		SearchEntry entry = saveJestEntryToElasticSearch();
		JestResult result = this.jestClient
				.execute(new Search(
						"{\"query\":{\"bool\":{\"must\":[{\"term\":{\"site.rawContent\":\"raw\"}}]}},\"from\":0,\"size\":10}"));

		List<SearchEntry> list = result
				.<List<SearchEntry>> getSourceAsObjectList(SearchEntry.class);

		Page<SearchEntry> results = new PageImpl<SearchEntry>(list);
		assertThat(results.getContent().get(0).getRawContent(),
				is(equalTo(entry.getRawContent())));
	}

	@Test
	public void testSearch() {
		SearchEntry result = saveSearchEntryToElasticSearch();

		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(matchAllQuery())
				.withFilter(
						FilterBuilders.andFilter(
								FilterBuilders.numericRangeFilter("publishAt").lte(
										new Date().getTime()), FilterBuilders.orFilter(
										FilterBuilders.queryFilter(matchPhraseQuery(
												"title", "in week")), FilterBuilders
												.queryFilter(matchPhraseQuery(
														"rawContent", "asdf"))))).build();

		this.elasticsearchTemplate.refresh(SearchEntry.class, true);

		Page<SearchEntry> results = this.elasticsearchTemplate.queryForPage(searchQuery,
				SearchEntry.class);
		assertThat(results.getContent().get(0).getRawContent(),
				is(equalTo(result.getRawContent())));
	}

	private SearchEntry saveSearchEntryToElasticSearch() {
		SearchEntry entry = SearchEntryBuilder.entry().title("This week in Spring")
				.rawContent("This is some raw content").build();

		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId("1");
		indexQuery.setObject(entry);

		this.elasticsearchTemplate.index(indexQuery);
		return entry;
	}

	private SearchEntry saveJestEntryToElasticSearch() throws Exception {
		SearchEntry entry = SearchEntryBuilder.entry().title("This week in Spring")
				.rawContent("This is some raw content").build();

		this.jestClient.execute(new Index.Builder(entry).id("2").index("site")
				.type("site").build());
		return entry;
	}

}
