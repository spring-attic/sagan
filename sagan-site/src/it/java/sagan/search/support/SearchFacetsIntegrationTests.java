package sagan.search.support;

import sagan.search.SearchEntry;
import saganx.AbstractIntegrationTests;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.searchbox.client.JestClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SearchFacetsIntegrationTests extends AbstractIntegrationTests {

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

    private SearchEntry springFrameworkApiDoc = SearchEntryBuilder.entry()
            .path("http://example.com/projects/springFramework/apiDoc")
            .title("a title")
            .facetPath("Projects")
            .facetPath("Projects/Api")
            .facetPath("Projects/SpringFramework")
            .facetPath("Projects/SpringFramework/3.4.5.RELEASE").build();

    private SearchEntry springFrameworkRefDoc = SearchEntryBuilder.entry()
            .path("http://example.com/projects/springFramework/refDoc")
            .title("a title")
            .facetPath("Projects")
            .facetPath("Projects/Reference")
            .facetPath("Projects/SpringFramework")
            .facetPath("Projects/SpringFramework/3.4.5.RELEASE").build();

    private SearchEntry springSecurityApiDoc = SearchEntryBuilder.entry()
            .path("http://example.com/projects/spring-security/apiDoc")
            .title("a title")
            .facetPath("Projects")
            .facetPath("Projects/Api")
            .facetPath("Projects/SpringSecurity")
            .facetPath("Projects/SpringSecurity/1.2.3.RELEASE").build();

    private SearchEntry springSecurityRefDoc = SearchEntryBuilder.entry()
            .path("http://example.com/projects/spring-security/refDoc")
            .title("a title")
            .facetPath("Projects")
            .facetPath("Projects/Reference")
            .facetPath("Projects/SpringSecurity")
            .facetPath("Projects/SpringSecurity/1.2.3.RELEASE").build();

    @Value("${elasticsearch.client.index}")
    private String index;
    private SearchIndexSetup searchIndexSetup;

    @Before
    public void setUp() throws Exception {
        searchIndexSetup = new SearchIndexSetup(jestClient, index);
        searchIndexSetup.deleteIndex();
        searchIndexSetup.createIndex();
    }

    @After
    public void tearDown() throws Exception {
        searchIndexSetup.deleteIndex();
        searchIndexSetup.createIndex();
    }

    @Test
    public void searchReturnsFacetInformation() throws ParseException {
        searchService.saveToIndex(gettingStarted);
        searchService.saveToIndex(blog);

        List<String> emptyFacetFilters = Collections.emptyList();
        SearchResults searchResults = searchService.search("title", pageable, emptyFacetFilters);

        List<SearchResult> content = searchResults.getPage().getContent();
        assertThat(content.size(), equalTo(2));
        assertThat(searchResults.getFacets().size(), equalTo(2));
        assertThat(searchResults.getFacets().get(0).getName(), equalTo("Blog"));
        assertThat(searchResults.getFacets().get(1).getName(), equalTo("Guides"));
    }

    @Test
    public void filterByFacetsReturnsFilteredContent() throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(gettingStarted);
        searchService.saveToIndex(blog);

        List<String> facetFilter = Arrays.asList("Guides");
        SearchResults searchResults = searchService.search("title", pageable, facetFilter);

        List<SearchResult> content = searchResults.getPage().getContent();
        assertThat(content.size(), equalTo(2));
    }

    @Test
    public void filterByMultipleFacetsAllowsMatchesFromUnrelatedFacets() throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(gettingStarted);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(springSecurityApiDoc);
        searchService.saveToIndex(springSecurityRefDoc);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Guides/Tutorials");
        facetPathFilters.add("Blog");
        facetPathFilters.add("Projects/SpringSecurity");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
        List<SearchResult> results = searchResults.getPage().getContent();

        assertThatResultsContains(results, tutorial, blog, springSecurityApiDoc, springSecurityRefDoc);
    }

    private void assertThatResultsContains(List<SearchResult> results, SearchEntry... entries) {
        ArrayList<String> resultPaths = new ArrayList<>();
        for (SearchResult result : results) {
            resultPaths.add(result.getPath());
        }

        ArrayList<String> expectedPaths = new ArrayList<>();
        for (SearchEntry entry : entries) {
            expectedPaths.add(entry.getPath());
        }

        assertThat(resultPaths, containsInAnyOrder(expectedPaths.toArray()));
    }

    @Test
    public void filterByMultipleFacetsProjectAndApi_restrictsProjectsToApiAndIncludesAllOtherFacets()
            throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(springFrameworkApiDoc);
        searchService.saveToIndex(springFrameworkRefDoc);
        searchService.saveToIndex(springSecurityApiDoc);
        searchService.saveToIndex(springSecurityRefDoc);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Blog");
        facetPathFilters.add("Projects/Api");
        facetPathFilters.add("Projects/SpringSecurity/1.2.3.RELEASE");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
        List<SearchResult> results = searchResults.getPage().getContent();

        assertThatResultsContains(results, blog, springSecurityApiDoc);
    }

    @Test
    public void filterByMultipleFacetsProjectAndReference_restrictsProjectsToReferenceAndIncludesAllOtherFacets()
            throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(springFrameworkApiDoc);
        searchService.saveToIndex(springFrameworkRefDoc);
        searchService.saveToIndex(springSecurityApiDoc);
        searchService.saveToIndex(springSecurityRefDoc);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Blog");
        facetPathFilters.add("Projects/Reference");
        facetPathFilters.add("Projects/SpringFramework");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
        List<SearchResult> results = searchResults.getPage().getContent();

        assertThatResultsContains(results, blog, springFrameworkRefDoc);
    }

    @Test
    public void filterByMultipleProjectVersionsFacets() throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(springFrameworkApiDoc);
        searchService.saveToIndex(springFrameworkRefDoc);
        searchService.saveToIndex(springSecurityApiDoc);
        searchService.saveToIndex(springSecurityRefDoc);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Blog");
        facetPathFilters.add("Projects/SpringFramework/3.4.5.RELEASE");
        facetPathFilters.add("Projects/SpringSecurity/1.2.3.RELEASE");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
        List<SearchResult> results = searchResults.getPage().getContent();

        assertThatResultsContains(results, blog, springFrameworkApiDoc, springFrameworkRefDoc, springSecurityApiDoc,
                springSecurityRefDoc);
    }

    @Test
    public void returnedFacetsAreNotAffectedByTheFacetFilters() throws ParseException {
        searchService.saveToIndex(blog);
        searchService.saveToIndex(gettingStarted);
        searchService.saveToIndex(tutorial);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Blog");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
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

        searchService.saveToIndex(unpublishedPost);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(gettingStarted);

        SearchResults searchResults = searchService.search("title", pageable, new ArrayList<String>());

        List<SearchResult> results = searchResults.getPage().getContent();
        assertThat(results.size(), equalTo(2));

        List<SearchFacet> facets = searchResults.getFacets();
        assertThat(facets.size(), equalTo(2));
        assertThat(facets.get(0).getName(), equalTo("Blog"));
        assertThat(facets.get(0).getCount(), equalTo(1));
        assertThat(facets.get(1).getName(), equalTo("Guides"));
    }

}
