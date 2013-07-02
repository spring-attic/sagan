package org.springframework.site.integration.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.site.blog.PaginationInfo;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.blog.web.BlogPostsPageRequest;
import org.springframework.site.blog.web.PostView;
import org.springframework.site.services.DateService;
import org.springframework.test.configuration.ElasticsearchStubConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.View;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ElasticsearchStubConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
public class SearchViewTests {

	@Autowired
	private	ThymeleafViewResolver viewResolver;
	private Map<String,Object> model;
	private View view;
	private MockHttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		view = viewResolver.resolveViewName("search/results", Locale.UK);
		response = new MockHttpServletResponse();
		Page<Post> posts = new PageImpl<Post>(Collections.<Post>emptyList(), BlogPostsPageRequest.forSearch(1), 0);

		model = new HashMap<String, Object>();
		model.put("results", Collections.emptyList());
		model.put("query", "searchterm");
		model.put("paginationInfo", new PaginationInfo(posts));
	}

	@Test
	public void searchBoxContainsUserQuery() throws Exception {
		view.render(model, new MockHttpServletRequest(), response);

		Document html = Jsoup.parse(response.getContentAsString());
		Element searchInputBox = html.select("form input[name=q]").first();
		assertThat(searchInputBox, is(notNullValue()));
		assertThat(searchInputBox.val(), is("searchterm"));
	}

	@Test
	public void displaysMessageIfNoResults() throws Exception {
		view.render(model, new MockHttpServletRequest(), response);

		Document html = Jsoup.parse(response.getContentAsString());
		Element searchInputBox = html.select("ul.results").first();
		assertThat(searchInputBox, is(nullValue()));

		Element message = html.select("#content .warning").first();
		assertThat(message.text(), is(notNullValue()));
	}

	@Test
	public void displaysSearchResults() throws Exception {
		model.put("results", Arrays.asList(createSinglePost()));
		view.render(model, new MockHttpServletRequest(), response);

		Document html = Jsoup.parse(response.getContentAsString());
		Element searchInputBox = html.select("ul.results li").first();
		assertThat(searchInputBox, is(notNullValue()));

		Element message = html.select("#content .warning").first();
		assertThat(message, is(nullValue()));
	}

	@Test
	public void displaysPaginationControl() throws Exception {
		Page<Post> posts = new PageImpl<Post>(buildManyPostsInNovember(10), BlogPostsPageRequest.forSearch(1), 11);
		model.put("results", posts.getContent());
		model.put("paginationInfo", new PaginationInfo(posts));
		view.render(model, new MockHttpServletRequest(), response);

		Document html = Jsoup.parse(response.getContentAsString());
		Element searchInputBox = html.select("#pagination_control").first();
		assertThat(searchInputBox, is(notNullValue()));
	}

	private PostView createSinglePost() {
		Post post = new PostBuilder().title("This week in Spring - June 3, 2013")
				.rawContent("raw content")
				.renderedContent("Html content")
				.renderedSummary("Html summary")
				.build();

		return new PostView(post, new DateService());
	}

	private List<Post> buildManyPostsInNovember(int numPostsToCreate) {
		Calendar calendar = Calendar.getInstance();
		List<Post> posts = new ArrayList<Post>();
		for (int postNumber = 1; postNumber <= numPostsToCreate; postNumber++) {
			calendar.set(2012, 10, postNumber);
			Post post = new PostBuilder().title("This week in Spring - November " + postNumber + ", 2012")
					.rawContent("Raw content")
					.renderedContent("Html content")
					.renderedSummary("Html summary")
					.dateCreated(calendar.getTime())
					.publishAt(calendar.getTime())
					.build();
			posts.add(post);
		}
		return posts;
	}
}