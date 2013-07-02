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
import org.springframework.site.blog.web.PostView;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.search.SearchController;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.ExtendedModelMap;

import java.text.ParseException;
import java.util.Date;
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
	private Post post;
	ExtendedModelMap model = new ExtendedModelMap();

	@Before
	public void setUp() throws Exception {
		elasticsearchTemplate.deleteIndex(Post.class);
		post = createSinglePost();
		addPostToIndex(post, "1");
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
		searchController.search("raw content", 1, model);

		List<PostView> posts = (List<PostView>) model.get("results");
		assertThat(posts, not(empty()));
		assertThat(posts.get(0).getRenderedContent(), is(equalTo(post.getRenderedContent())));
	}

	@Test
	public void searchOnlyIncludesPostsMatchingSearchterm() throws ParseException {
		Post draftPost = createSinglePost();
		draftPost.setTitle("Test");
		draftPost.setRawContent("Test body");
		addPostToIndex(draftPost, "2");

		searchController.search("content", 1, model);

		List<PostView> posts = (List<PostView>) model.get("results");
		assertThat(posts.size(), equalTo(1));
	}

	@Test
	public void searchDoesNotIncludeDraftPosts() throws ParseException {
		Post draftPost = createSinglePost();
		draftPost.setDraft(true);
		addPostToIndex(draftPost, "2");

		searchController.search("content", 1, model);

		List<Post> posts = (List<Post>) model.get("results");
		assertThat(posts, not(empty()));
		assertThat(posts.size(), equalTo(1));
	}

	@Test
	public void searchDoesNotIncludeUnpublishedPosts() throws ParseException {
		Post draftPost = createSinglePost();
		draftPost.setPublishAt(new Date(System.currentTimeMillis() + 100000000));
		addPostToIndex(draftPost, "2");

		searchController.search("content", 1, model);

		List<Post> posts = (List<Post>) model.get("results");
		assertThat(posts, not(empty()));
		assertThat(posts.size(), equalTo(1));
	}

	private void addPostToIndex(Post post, String id) {
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId(id);
		indexQuery.setObject(post);
		elasticsearchTemplate.index(indexQuery);
		elasticsearchTemplate.refresh(Post.class, true);
	}

	private Post createSinglePost() throws ParseException {
		Post post = new PostBuilder().title("This week in Spring")
				.rawContent("raw content")
				.renderedContent("Html content")
				.renderedSummary("Html summary")
				.publishAt("2013-01-01 10:00")
				.build();
		return post;
	}

}
