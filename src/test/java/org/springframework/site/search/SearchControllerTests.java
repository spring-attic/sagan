package org.springframework.site.search;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPageImpl;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.blog.web.PostView;
import org.springframework.site.blog.web.PostViewFactory;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class SearchControllerTests {

	@Mock
	private ElasticsearchTemplate elasticsearchTemplate;

	@Mock
	private PostViewFactory postViewFactory;

	private SearchController controller;
	private ExtendedModelMap model = new ExtendedModelMap();
	private FacetedPageImpl<Post> posts;
	private List<PostView> postViews;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new SearchController(elasticsearchTemplate, postViewFactory);
		List<Post> postsList = new ArrayList<Post>();
		postsList.add(PostBuilder.post().build());
		posts = new FacetedPageImpl<Post>(postsList);
		postViews = new ArrayList<PostView>();
		when(elasticsearchTemplate.queryForPage(any(CriteriaQuery.class), eq(Post.class))).thenReturn(posts);
		when(postViewFactory.createPostViewList(posts.getContent())).thenReturn(postViews);
	}

	@Test
	public void search_providesQueryInModel() {
		controller.search("searchTerm", 1, model);
		assertThat((String) model.get("query"), is(equalTo("searchTerm")));
	}

	@Test
	public void search_providesPaginationInfoInModel() {
		controller.search("searchTerm", 1, model);
		assertThat(model.get("paginationInfo"), is(notNullValue()));
	}

	@Test
	public void search_providesResultsInModel() {
		controller.search("searchTerm", 1, model);
		assertThat((List<PostView>) model.get("results"), equalTo(postViews));
	}

	@Test
	public void search_providesAllResultsForBlankQuery() {
		when(elasticsearchTemplate.queryForPage(any(SearchQuery.class), eq(Post.class))).thenReturn(posts);
		controller.search("", 1, model);
		assertThat((List<PostView>) model.get("results"), equalTo(postViews));
	}
}
