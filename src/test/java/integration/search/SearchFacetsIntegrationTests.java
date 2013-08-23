package integration.search;

import integration.IntegrationTestBase;
import io.searchbox.client.JestClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchResult;
import org.springframework.site.search.SearchResults;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.search.SearchEntryBuilder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class SearchFacetsIntegrationTests extends IntegrationTestBase {

	private final Pageable pageable = new PageRequest(0, 10);

	@Autowired
	private SearchService searchService;

	@Autowired
	private JestClient jestClient;

	private SearchEntry gettingStarted = SearchEntryBuilder.entry()
			.path("http://example.com/gettingStarted/some")
			.title("a title")
			.facetPath("Guides")
			.facetPath("Guides/Getting Started").notCurrent().build();


	private SearchEntry tutorial = SearchEntryBuilder.entry()
			.path("http://example.com/tutorial")
			.title("a title")
			.facetPath("Guides")
			.facetPath("Guides/Tutorials").build();


	private SearchEntry blog = SearchEntryBuilder.entry()
			.path("http://example.com/blog")
			.title("a title")
			.facetPath("Blog")
			.facetPath("Blog/Engineering").build();

	private SearchEntry apiDoc = SearchEntryBuilder.entry()
			.path("http://example.com/projects/apiDoc")
			.title("a title")
			.facetPath("Projects")
			.facetPath("Projects/Api")
			.facetPath("Projects/SpringFramework")
			.facetPath("Projects/SpringFramework/3.4.5.RELEASE").build();

	private SearchEntry refDoc = SearchEntryBuilder.entry()
			.path("http://example.com/projects/refDoc")
			.title("a title")
			.facetPath("Projects")
			.facetPath("Projects/Reference")
			.facetPath("Projects/SpringSecurity")
			.facetPath("Projects/SpringSecurity/1.2.3.RELEASE").build();

	@Before
	public void setUp() throws Exception {
		SearchIndexSetup searchIndexSetup = new SearchIndexSetup(this.jestClient);
		searchIndexSetup.deleteIndex();
		searchIndexSetup.createIndex();
	}

	@Test
	public void searchReturnsFacetInformation() throws ParseException {
		this.searchService.saveToIndex(gettingStarted);
		this.searchService.saveToIndex(blog);

		SearchResults searchResults = this.searchService.search("title", this.pageable, Collections.<String>emptyList());

		List<SearchResult> content = searchResults.getPage().getContent();
		assertThat(content.size(), equalTo(2));
		assertThat(searchResults.getFacets().size(), equalTo(2));
		assertThat(searchResults.getFacets().get(0).getName(), equalTo("Blog"));
		assertThat(searchResults.getFacets().get(1).getName(), equalTo("Guides"));
	}

	@Test
	public void filterByFacetsForOneFacetOnly() throws ParseException {
		this.searchService.saveToIndex(tutorial);
		this.searchService.saveToIndex(gettingStarted);
		this.searchService.saveToIndex(blog);

		List<String> facetFilter = Arrays.asList("Guides");
		SearchResults searchResults = this.searchService.search("title", this.pageable, facetFilter);

		List<SearchResult> content = searchResults.getPage().getContent();
		assertThat(content.size(), equalTo(2));
		assertThat(searchResults.getFacets().size(), equalTo(1));
		assertThat(searchResults.getFacets().get(0).getName(), equalTo("Guides"));
	}

	@Test
	public void filterByMultipleFacetsAllowsMatchesFromUnrelatedFacets() throws ParseException {
		this.searchService.saveToIndex(tutorial);
		this.searchService.saveToIndex(gettingStarted);
		this.searchService.saveToIndex(blog);
		this.searchService.saveToIndex(apiDoc);
		this.searchService.saveToIndex(refDoc);

		List<String> facetPathFilters = new ArrayList<>();
		facetPathFilters.add("Guides/Tutorials");
		facetPathFilters.add("Blog");
		facetPathFilters.add("Projects/Api");
		facetPathFilters.add("Projects/SpringSecurity/1.2.3.RELEASE");

		SearchResults searchResults = this.searchService.search("title", this.pageable, facetPathFilters);
		List<SearchResult> results = searchResults.getPage().getContent();

		assertThat(results.size(), equalTo(4));

		ArrayList<String> paths = new ArrayList<>();
		for (SearchResult result : results) {
			paths.add(result.getPath());
		}

		assertThat(paths, containsInAnyOrder(
				tutorial.getPath(),
				blog.getPath(),
				apiDoc.getPath(),
				refDoc.getPath())
		);
	}

}
