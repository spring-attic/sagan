package integration.search;

import integration.IntegrationTestBase;
import io.searchbox.client.JestClient;
import io.spring.site.search.SearchEntry;
import io.spring.site.search.SearchFacet;
import io.spring.site.search.SearchResult;
import io.spring.site.search.SearchResults;
import io.spring.site.search.SearchService;
import io.spring.site.web.search.SearchEntryBuilder;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import utils.SetSystemProperty;

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

	@Value("${elasticsearch.client.index}")
	private String index;

	@Before
	public void setUp() throws Exception {
		SearchIndexSetup searchIndexSetup = new SearchIndexSetup(this.jestClient, index);
		searchIndexSetup.deleteIndex();
		searchIndexSetup.createIndex();
	}

	@Test
	public void searchReturnsFacetInformation() throws ParseException {
		this.searchService.saveToIndex(gettingStarted);
		this.searchService.saveToIndex(blog);

		List<String> emptyFacetFilters = Collections.emptyList();
		SearchResults searchResults = this.searchService.search("title", this.pageable, emptyFacetFilters);

		List<SearchResult> content = searchResults.getPage().getContent();
		assertThat(content.size(), equalTo(2));
		assertThat(searchResults.getFacets().size(), equalTo(2));
		assertThat(searchResults.getFacets().get(0).getName(), equalTo("Blog"));
		assertThat(searchResults.getFacets().get(1).getName(), equalTo("Guides"));
	}

	@Test
	public void filterByFacetsReturnsFilteredContent() throws ParseException {
		this.searchService.saveToIndex(tutorial);
		this.searchService.saveToIndex(gettingStarted);
		this.searchService.saveToIndex(blog);

		List<String> facetFilter = Arrays.asList("Guides");
		SearchResults searchResults = this.searchService.search("title", this.pageable, facetFilter);

		List<SearchResult> content = searchResults.getPage().getContent();
		assertThat(content.size(), equalTo(2));
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

	@Test
	public void returnedFacetsAreNotAffectedByTheFacetFilters() throws ParseException {
		this.searchService.saveToIndex(blog);
		this.searchService.saveToIndex(gettingStarted);
		this.searchService.saveToIndex(tutorial);

		List<String> facetPathFilters = new ArrayList<>();
		facetPathFilters.add("Blog");

		SearchResults searchResults = this.searchService.search("title", this.pageable, facetPathFilters);
		List<SearchFacet> facets = searchResults.getFacets();

		assertThat(facets.size(), equalTo(2));
		assertThat(facets.get(0).getName(), equalTo("Blog"));
		assertThat(facets.get(0).getCount(), equalTo(1));
		assertThat(facets.get(1).getName(), equalTo("Guides"));
		assertThat(facets.get(1).getCount(), equalTo(2));
	}

	@Test
	public void unpublishedEntriesDoNotAppearInResultsOrFacets() throws ParseException {
		SearchEntry unpublishedPost = SearchEntryBuilder.entry()
				.path("http://example.com/blog")
				.title("a title")
				.publishAt("2100-12-01 12:32")
				.facetPath("Blog")
				.facetPath("Blog/Engineering").build();

		this.searchService.saveToIndex(unpublishedPost);
		this.searchService.saveToIndex(blog);
		this.searchService.saveToIndex(gettingStarted);

		SearchResults searchResults = this.searchService.search("title", this.pageable, new ArrayList<String>());

		List<SearchResult> results = searchResults.getPage().getContent();
		assertThat(results.size(), equalTo(2));

		List<SearchFacet> facets = searchResults.getFacets();
		assertThat(facets.size(), equalTo(2));
		assertThat(facets.get(0).getName(), equalTo("Blog"));
		assertThat(facets.get(0).getCount(), equalTo(1));
		assertThat(facets.get(1).getName(), equalTo("Guides"));
	}

}
