package org.springframework.site.integration.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
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
		model = new HashMap<String, Object>();
		model.put("results", Collections.emptyList());
		model.put("query", "searchterm");
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

	private Post createSinglePost() {
		Post post = new PostBuilder().title("This week in Spring - June 3, 2013")
				.rawContent("raw content")
				.renderedContent("Html content")
				.renderedSummary("Html summary")
				.build();

		return post;
	}
}