package integration.search;

import integration.IntegrationTestBase;
import io.searchbox.client.JestClient;
import sagan.search.SearchEntry;
import sagan.search.SearchResult;
import sagan.search.service.SearchService;
import sagan.search.SearchEntryBuilder;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sagan.search.SearchIndexSetup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class SearchServiceIntegrationTests extends IntegrationTestBase {

    private final Pageable pageable = new PageRequest(0, 10);

    @Autowired
    private SearchService searchService;

    @Autowired
    private JestClient jestClient;

    private SearchEntry entry;

    @Value("${elasticsearch.client.index}")
    private String index;
    private SearchIndexSetup searchIndexSetup;

    @Before
    public void setUp() throws Exception {
        searchIndexSetup = new SearchIndexSetup(this.jestClient, this.index);
        searchIndexSetup.deleteIndex();
        searchIndexSetup.createIndex();
    }

    @After
    public void tearDown() throws Exception {
        searchIndexSetup.deleteIndex();
        searchIndexSetup.createIndex();
    }

    private void indexSingleEntry() throws ParseException {
        this.entry = createSingleEntry("/some/path");
        this.searchService.saveToIndex(this.entry);
    }

    private SearchEntry createSingleEntry(String path) throws ParseException {
        return SearchEntryBuilder.entry().path(path).title("This week in Spring")
                .rawContent("raw content").summary("Html summary")
                .publishAt("2013-01-01 10:00").build();
    }

    private void assertThatSearchReturnsEntry(String query) {
        Page<SearchResult> searchEntries = this.searchService.search(query,
                this.pageable, Collections.<String> emptyList()).getPage();
        List<SearchResult> entries = searchEntries.getContent();
        assertThat(entries, not(empty()));
        assertThat(entries.get(0).getTitle(), is(equalTo(this.entry.getTitle())));
    }

    @Test
    public void testSearchAll() throws ParseException {
        indexSingleEntry();
        assertThatSearchReturnsEntry("");
    }

    @Test
    public void testSearchContent() throws ParseException {
        indexSingleEntry();
        assertThatSearchReturnsEntry("raw");
    }

    @Test
    public void testSearchTitle() throws ParseException {
        indexSingleEntry();
        assertThatSearchReturnsEntry("Spring");
    }

    @Test
    public void testSearchWithMultipleWords() throws ParseException {
        indexSingleEntry();
        assertThatSearchReturnsEntry("raw content");
    }

    @Test
    public void searchOnlyIncludesEntriesMatchingSearchTerm() throws ParseException {
        indexSingleEntry();

        SearchEntry secondEntry = SearchEntryBuilder.entry().path("/another/path")
                .title("Test").rawContent("Test body").build();

        this.searchService.saveToIndex(secondEntry);
        Page<SearchResult> searchEntries = this.searchService.search("content",
                this.pageable, Collections.<String> emptyList()).getPage();
        List<SearchResult> entries = searchEntries.getContent();
        assertThat(entries.size(), equalTo(1));
    }

    @Test
    public void searchPagesProperly() throws ParseException {
        SearchEntryBuilder builder = SearchEntryBuilder.entry().rawContent("raw content")
                .summary("Html summary").publishAt("2013-01-01 10:00");

        SearchEntry entry1 = builder.path("item1").title("Item 1").build();
        this.searchService.saveToIndex(entry1);

        SearchEntry entry2 = builder.path("item2").title("Item 2").build();
        this.searchService.saveToIndex(entry2);

        Pageable page1 = new PageRequest(0, 1);
        Page<SearchResult> searchEntries1 = this.searchService.search("content", page1,
                Collections.<String> emptyList()).getPage();
        assertThat(searchEntries1.getContent().get(0).getId(), equalTo(entry1.getId()));

        Pageable page2 = new PageRequest(1, 1);
        Page<SearchResult> searchEntries2 = this.searchService.search("content", page2,
                Collections.<String> emptyList()).getPage();
        assertThat(searchEntries2.getContent().get(0).getId(), equalTo(entry2.getId()));
    }

    @Test
    public void searchQueryReportsPageTotals() throws Exception {
        SearchEntryBuilder builder = SearchEntryBuilder.entry().rawContent("raw content")
                .summary("Html summary").publishAt("2013-01-01 10:00");

        for (int i = 0; i < 25; i++) {
            SearchEntry entry = builder.path("item" + i).title("Item " + i).build();
            this.searchService.saveToIndex(entry);
        }

        int page = 1;
        Pageable pageable = new PageRequest(page, 10);
        Page<SearchResult> searchEntries = this.searchService.search("", pageable,
                Collections.<String> emptyList()).getPage();
        assertThat(searchEntries.getContent().size(), equalTo(10));
        assertThat(searchEntries.getTotalPages(), equalTo(3));
        assertThat(searchEntries.getNumber(), equalTo(page));
    }

    @Test
    public void searchThatReturnsNoResultsIsEmpty() throws ParseException {
        indexSingleEntry();
        Pageable page = new PageRequest(0, 10);
        Page<SearchResult> searchEntries = this.searchService.search(
                "somethingthatwillneverappearsupercalifragilousIcantspelltherest", page,
                Collections.<String> emptyList()).getPage();
        assertThat(searchEntries.getContent().size(), equalTo(0));
        assertThat(searchEntries.getTotalPages(), equalTo(0));
    }

    @Test
    public void searchisCaseInsensitive() throws ParseException {
        this.entry = SearchEntryBuilder.entry().path("http://example.com")
                .title("My Entry").rawContent("Uppercase is here")
                .summary("Html summary").publishAt("2013-01-01 10:00").build();
        this.searchService.saveToIndex(this.entry);

        assertThatSearchReturnsEntry("uppercase");
    }

    @Test
    public void searchMatchesPartialWords() throws ParseException {
        this.entry = SearchEntryBuilder.entry().path("http://example.com")
                .title("My Entry").rawContent("Exporter is here")
                .summary("Html summary").publishAt("2013-01-01 10:00").build();
        this.searchService.saveToIndex(this.entry);

        assertThatSearchReturnsEntry("export");
    }

    @Test
    public void boostsTitleMatchesOverContent() throws ParseException {
        SearchEntry entryContent = SearchEntryBuilder.entry()
                .path("http://example.com/content").title("a title")
                .rawContent("application is in the content").summary("Html summary")
                .publishAt("2013-01-01 10:00").build();

        this.searchService.saveToIndex(entryContent);

        SearchEntry entryTitle = SearchEntryBuilder.entry()
                .path("http://example.com/title").title("application is in the title")
                .rawContent("some content").summary("Html summary")
                .publishAt("2013-01-01 10:00").build();

        this.searchService.saveToIndex(entryTitle);

        List<SearchResult> results = this.searchService
                .search("application", this.pageable, Collections.<String> emptyList())
                .getPage().getContent();
        assertThat(results.get(0).getId(), is(entryTitle.getId()));
        assertThat(results.get(1).getId(), is(entryContent.getId()));
    }

    @Test
    public void boostsCurrentVersionEntries() throws ParseException {
        SearchEntry notCurrent = SearchEntryBuilder.entry()
                .path("http://example.com/content").title("a title")
                .rawContent("application is in the content").summary("Html summary")
                .publishAt("2013-01-01 10:00").notCurrent().build();

        this.searchService.saveToIndex(notCurrent);

        SearchEntry current = SearchEntryBuilder.entry()
                .path("http://example.com/another_one").title("a title")
                .rawContent("application is in the content").summary("Html summary")
                .publishAt("2013-01-01 10:00").build();

        this.searchService.saveToIndex(current);

        List<SearchResult> results = this.searchService
                .search("application", this.pageable, Collections.<String> emptyList())
                .getPage().getContent();
        assertThat(results.get(0).getId(), is(current.getId()));
        assertThat(results.get(1).getId(), is(notCurrent.getId()));
    }

    @Test
    public void returnsApiDocSearchResult() throws ParseException {
        SearchEntry apiDoc = SearchEntryBuilder.entry()
                .path("http://example.com/content").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .notCurrent().type("apiDoc").build();

        this.searchService.saveToIndex(apiDoc);

        List<SearchResult> results = this.searchService
                .search("ApplicationContext", this.pageable,
                        Collections.<String> emptyList()).getPage().getContent();
        assertThat(results.get(0).getId(), is(apiDoc.getId()));
        assertThat(results.get(0).getSummary(), is("class level description"));
        assertThat(results.get(0).getHighlight(), containsString("Application"));
        assertThat(results.get(0).getHighlight(), containsString("Context"));
        assertThat(results.get(0).getOriginalSearchTerm(), equalTo("ApplicationContext"));
    }

    @Test
    public void deleteOldApiDocs() throws ParseException {
        SearchEntry oldApiDoc1 = SearchEntryBuilder.entry()
                .path("http://example.com/content1").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .version("1.2.3.RELEASE").notCurrent().projectId("project id to delete")
                .type("apiDoc").build();

        this.searchService.saveToIndex(oldApiDoc1);

        SearchEntry oldApiDoc2 = SearchEntryBuilder.entry()
                .path("http://example.com/content2").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .version("1.5.3.M1").notCurrent().projectId("project id to delete")
                .type("apiDoc").build();

        this.searchService.saveToIndex(oldApiDoc2);

        SearchEntry newApiDoc1 = SearchEntryBuilder.entry()
                .path("http://example.com/content3").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .version("2.0.0.RELEASE").notCurrent().projectId("project id to delete")
                .type("apiDoc").build();

        this.searchService.saveToIndex(newApiDoc1);

        SearchEntry newApiDoc2 = SearchEntryBuilder.entry()
                .path("http://example.com/content4").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .version("2.1.0.M1").notCurrent().projectId("project id to delete")
                .type("apiDoc").build();

        this.searchService.saveToIndex(newApiDoc2);

        SearchEntry nonApiDoc = SearchEntryBuilder.entry()
                .path("http://example.com/content5").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .notCurrent().type("site").build();

        this.searchService.saveToIndex(nonApiDoc);

        SearchEntry otherApiDoc = SearchEntryBuilder.entry()
                .path("http://example.com/content6").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .notCurrent().projectId("not id to delete").version("3.4.5.RELEASE")
                .type("apiDoc").build();

        this.searchService.saveToIndex(otherApiDoc);

        this.searchService.removeOldProjectEntriesFromIndex("project id to delete",
                Arrays.asList("2.0.0.RELEASE", "2.1.0.M1"));

        List<SearchResult> results = this.searchService
                .search("ApplicationContext", this.pageable,
                        Collections.<String> emptyList()).getPage().getContent();
        assertThat(results.size(), equalTo(4));
    }

    @Test
    public void deleteOldReferenceDocs() throws ParseException {
        SearchEntry oldReferenceDoc1 = SearchEntryBuilder.entry()
                .path("http://example.com/content1").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .version("1.2.3.RELEASE").notCurrent().projectId("project id to delete")
                .type("site").build();

        this.searchService.saveToIndex(oldReferenceDoc1);

        SearchEntry oldReferenceDoc2 = SearchEntryBuilder.entry()
                .path("http://example.com/content2").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .version("1.5.3.M1").notCurrent().projectId("project id to delete")
                .type("site").build();

        this.searchService.saveToIndex(oldReferenceDoc2);

        SearchEntry newReferenceDoc1 = SearchEntryBuilder.entry()
                .path("http://example.com/content3").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .version("2.0.0.RELEASE").notCurrent().projectId("project id to delete")
                .type("site").build();

        this.searchService.saveToIndex(newReferenceDoc1);

        SearchEntry newReferenceDoc2 = SearchEntryBuilder.entry()
                .path("http://example.com/content4").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .version("2.1.0.M1").notCurrent().projectId("project id to delete")
                .type("site").build();

        this.searchService.saveToIndex(newReferenceDoc2);

        SearchEntry nonReferenceDoc = SearchEntryBuilder.entry()
                .path("http://example.com/content5").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .notCurrent().type("site").build();

        this.searchService.saveToIndex(nonReferenceDoc);

        SearchEntry othersite = SearchEntryBuilder.entry()
                .path("http://example.com/content6").title("ApplicationContext")
                .rawContent("This is an api doc for ApplicationContext.")
                .summary("class level description").publishAt("2013-01-01 10:00")
                .notCurrent().projectId("not id to delete").version("3.4.5.RELEASE")
                .type("site").build();

        this.searchService.saveToIndex(othersite);

        this.searchService.removeOldProjectEntriesFromIndex("project id to delete",
                Arrays.asList("2.0.0.RELEASE", "2.1.0.M1"));

        List<SearchResult> results = this.searchService
                .search("ApplicationContext", this.pageable,
                        Collections.<String> emptyList()).getPage().getContent();
        assertThat(results.size(), equalTo(4));
    }

}
