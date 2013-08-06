package integration.search;

import integration.IntegrationTestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchFacet;
import org.springframework.site.search.SearchResult;
import org.springframework.site.search.SearchResults;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.PaginationInfo;
import org.springframework.site.web.search.SearchEntryBuilder;
import org.springframework.site.web.search.SearchForm;
import org.springframework.site.web.search.SearchResultBuilder;
import org.springframework.web.servlet.View;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SearchViewTests extends IntegrationTestBase {

	@Autowired
	private ThymeleafViewResolver viewResolver;
	private Map<String, Object> model;
	private View view;
	private MockHttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		view = viewResolver.resolveViewName("search/results", Locale.UK);
		response = new MockHttpServletResponse();
		Page<SearchResult> results = new PageImpl<>(Collections.<SearchResult>emptyList(), PageableFactory.forSearch(1), 0);

		SearchForm searchForm = new SearchForm();
		searchForm.setQ("searchterm");

		model = new HashMap<>();
		model.put("results", Collections.emptyList());
		model.put("searchForm", searchForm);
		model.put("paginationInfo", new PaginationInfo(results));
		model.put("root", new SearchResults(results, Collections.<SearchFacet>emptyList()));
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
		model.put("results", Arrays.asList(createSingleSearchEntry()));
		view.render(model, new MockHttpServletRequest(), response);

		Document html = Jsoup.parse(response.getContentAsString());
		Element searchInputBox = html.select(".search-results div").first();
		assertThat(searchInputBox, is(notNullValue()));

		Element message = html.select("#content .warning").first();
		assertThat(message, is(nullValue()));
	}

	@Test
	public void displaysPaginationControl() throws Exception {
		Page<SearchResult> entries = new PageImpl<>(buildManySearchEntriesInNovember(10), PageableFactory.forSearch(1), 11);
		model.put("results", entries.getContent());
		model.put("paginationInfo", new PaginationInfo(entries));
		view.render(model, new MockHttpServletRequest(), response);

		Document html = Jsoup.parse(response.getContentAsString());
		Element searchInputBox = html.select("#pagination_control").first();
		assertThat(searchInputBox, is(notNullValue()));
	}

	private SearchResult createSingleSearchEntry() {
		return SearchResultBuilder.entry()
				.title("This week in Spring - June 3, 2013")
				.summary("Html summary")
				.rawContent("Raw Content")
				.path("/blog/" + 1)
				.build();
	}

	private List<SearchResult> buildManySearchEntriesInNovember(int numberToCreate) {
		Calendar calendar = Calendar.getInstance();
		List<SearchResult> entries = new ArrayList<>();
		for (int number = 1; number <= numberToCreate; number++) {
			calendar.set(2012, 10, number);

			SearchResult entry = SearchResultBuilder.entry()
					.title("This week in Spring - November " + number + ", 2012")
					.summary("Html summary")
					.rawContent("Raw Content")
					.type("site")
					.highlight("highlight")
					.path("/blog/" + number)
					.build();

			entries.add(entry);
		}
		return entries;
	}
}