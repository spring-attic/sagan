package org.springframework.site.integration.search;


import org.elasticsearch.index.query.FilterBuilders;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Ignore("Useful test for validating elasticsearch API")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
public class ElasticSearchTests {

	@Autowired
	private ElasticsearchOperations elasticsearchTemplate;

	@Before
	public void setUp() throws Exception {
		elasticsearchTemplate.deleteIndex(SearchEntry.class);
	}

	@Test
	public void testIndex() {
		SearchEntry result = saveSearchEntryToElasticSearch();

		CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("rawContent").contains("raw"));

		elasticsearchTemplate.refresh(SearchEntry.class, true);

		Page<SearchEntry> results = elasticsearchTemplate.queryForPage(criteriaQuery, SearchEntry.class);
		assertThat(results.getContent().get(0).getRawContent(), is(equalTo(result.getRawContent())));
	}


	@Test
	public void testSearch() {
		SearchEntry result = saveSearchEntryToElasticSearch();

		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFilter(
						FilterBuilders.andFilter(
								FilterBuilders.numericRangeFilter("publishAt").lte(new Date().getTime()),
								FilterBuilders.orFilter(
										FilterBuilders.queryFilter(matchPhraseQuery("title", "in week")),
										FilterBuilders.queryFilter(matchPhraseQuery("rawContent", "asdf"))
								)
						)
				).build();

		elasticsearchTemplate.refresh(SearchEntry.class, true);

		Page<SearchEntry> results = elasticsearchTemplate.queryForPage(searchQuery, SearchEntry.class);
		assertThat(results.getContent().get(0).getRawContent(), is(equalTo(result.getRawContent())));
	}

	private SearchEntry saveSearchEntryToElasticSearch() {
		SearchEntry entry = SearchEntryBuilder.entry()
				.title("This week in Spring")
				.rawContent("This is some raw content")
				.build();

		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId("1");
		indexQuery.setObject(entry);

		elasticsearchTemplate.index(indexQuery);
		return entry;
	}

}
