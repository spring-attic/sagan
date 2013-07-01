package org.springframework.site.integration.search;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.search.SearchController;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
public class SearchIntegrationTests {

	@Autowired
	private ElasticsearchOperations elasticsearchTemplate;

	@Autowired
	private SearchController searchController;

	@Before
	public void setUp() throws Exception {
		elasticsearchTemplate.deleteIndex(Post.class);
	}

	@Test
	public void testSearch() {
		Post post = createSinglePost();
		addPostToIndex(post);

		ExtendedModelMap model = new ExtendedModelMap();
		searchController.search("content", model);

		List<Post> posts = (List<Post>) model.get("results");
		assertThat(posts, not(empty()));
		assertThat(posts.get(0).getRawContent(), is(equalTo(post.getRawContent())));
	}

	private void addPostToIndex(Post post) {
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId("1");
		indexQuery.setObject(post);
		elasticsearchTemplate.index(indexQuery);
		elasticsearchTemplate.refresh(Post.class, true);
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
