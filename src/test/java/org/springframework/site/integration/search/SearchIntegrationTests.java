package org.springframework.site.integration.search;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.blog.web.PostView;
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
@Ignore
public class SearchIntegrationTests {

	@Autowired
	private ElasticsearchOperations elasticsearchTemplate;

	@Autowired
	private SearchController searchController;
	private Post post;
	ExtendedModelMap model = new ExtendedModelMap();

	@Before
	public void setUp() throws Exception {
		elasticsearchTemplate.deleteIndex(Post.class);
		post = createSinglePost();
		addPostToIndex(post);
	}

	@Test
	public void testSearch() {
		searchController.search("content", 1, model);

		List<PostView> posts = (List<PostView>) model.get("results");
		assertThat(posts, not(empty()));
		assertThat(posts.get(0).getRenderedContent(), is(equalTo(post.getRenderedContent())));
	}

	@Test
	public void testSearchTitle() {
		searchController.search("Spring", 1, model);

		List<PostView> posts = (List<PostView>) model.get("results");
		assertThat(posts, not(empty()));
		assertThat(posts.get(0).getRenderedContent(), is(equalTo(post.getRenderedContent())));
	}

	@Test
	public void testSearchWithMultipleWords() {
		searchController.search("content \nraw", 1, model);

		List<PostView> posts = (List<PostView>) model.get("results");
		assertThat(posts, not(empty()));
		assertThat(posts.get(0).getRenderedContent(), is(equalTo(post.getRenderedContent())));
	}

	private void addPostToIndex(Post post) {
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId("1");
		indexQuery.setObject(post);
		elasticsearchTemplate.index(indexQuery);
		elasticsearchTemplate.refresh(Post.class, true);
	}

	private Post createSinglePost() {
		Post post = new PostBuilder().title("This week in Spring")
				.rawContent("raw content")
				.renderedContent("Html content")
				.renderedSummary("Html summary")
				.build();

		return post;
	}

}
