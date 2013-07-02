package org.springframework.site.integration.search;


import org.elasticsearch.index.query.FilterBuilders;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.configuration.ApplicationConfiguration;
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
	private ElasticsearchTemplate elasticsearchTemplate;

	@Before
	public void setUp() throws Exception {
		elasticsearchTemplate.deleteIndex(Post.class);
	}

	@Test
	public void testIndex() {
		Post post = createSinglePost();

		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId("1");
		indexQuery.setObject(post);
		elasticsearchTemplate.index(indexQuery);

		CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("rawContent").contains("raw"));

		elasticsearchTemplate.refresh(Post.class, true);

		Page<Post> posts = elasticsearchTemplate.queryForPage(criteriaQuery, Post.class);
		assertThat(posts.getContent().get(0).getRawContent(), is(equalTo(post.getRawContent())));
	}


	@Test
	public void testSearch() {
		Post post = createSinglePost();
		// post.setDraft(true);
		// post.setPublishAt(new Date(System.currentTimeMillis() + 1000000000));

		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId("1");
		indexQuery.setObject(post);

		elasticsearchTemplate.index(indexQuery);

		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFilter(
						FilterBuilders.andFilter(FilterBuilders.termFilter("draft", Boolean.FALSE),
								FilterBuilders.numericRangeFilter("publishAt").lte(new Date().getTime()),
								FilterBuilders.orFilter(
										FilterBuilders.queryFilter(matchPhraseQuery("title", "in week")),
										FilterBuilders.queryFilter(matchPhraseQuery("rawContent", "asdf"))
								)
						)
				).build();

		elasticsearchTemplate.refresh(Post.class, true);

		Page<Post> posts = elasticsearchTemplate.queryForPage(searchQuery, Post.class);
		assertThat(posts.getContent().get(0).getRawContent(), is(equalTo(post.getRawContent())));
	}

	private Post createSinglePost() {
		Post post = new PostBuilder().title("This week in Spring - June 3, 2013")
				.rawContent("raw content")
				.renderedContent("Html content")
				.renderedSummary("Html summary")
				.build();

		return post;
	}

}
