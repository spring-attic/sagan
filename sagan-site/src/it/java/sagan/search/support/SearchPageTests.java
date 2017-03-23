package sagan.search.support;

import sagan.search.types.SearchEntry;

import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import saganx.AbstractIntegrationTests;

@ContextConfiguration(classes = InMemoryElasticSearchConfig.class)
public class SearchPageTests extends AbstractIntegrationTests {

    @Autowired
    private SearchService searchService;

    @Test
    public void displaysSearchResults() throws Exception {
        createSingleSearchEntry();

        Document html = performSearch("content");

        Element searchInputBox = html.select(".search-results div").first();
        assertThat(searchInputBox, is(notNullValue()));

        Element message = html.select("#content .warning").first();
        assertThat(message, is(nullValue()));
    }

    @Test
    public void searchBoxContainsUserQuery() throws Exception {
        Document html = performSearch("someterm");

        Element searchInputBox = html.select("form input[name=q]").first();
        assertThat(searchInputBox, is(notNullValue()));
        assertThat(searchInputBox.val(), is("someterm"));
    }

    @Test
    public void displaysPaginationControl() throws Exception {
        buildManySearchEntries(15);

        Document html = performSearch("content");
        Element paginationControl = html.select("#pagination_control").first();
        assertThat(paginationControl, is(notNullValue()));
    }

    @Test
    public void displaysMessageIfNoResults() throws Exception {
        Document html = performSearch("blahblahblah");

        Element searchInputBox = html.select("ul.results").first();
        assertThat(searchInputBox, is(nullValue()));

        Element message = html.select("#content .warning").first();
        assertThat(message.text(), is(notNullValue()));
    }

    private Document performSearch(String search) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/search?q=" + search))
                .andReturn();
        return Jsoup.parse(mvcResult.getResponse().getContentAsString());
    }

    private void createSingleSearchEntry() {
        SearchEntry searchEntry = SearchEntryBuilder.entry()
                .title("This week in Spring - June 3, 2013")
                .summary("Html summary")
                .rawContent("Raw Content")
                .path("/blog/" + 1)
                .blog();

        searchService.saveToIndex(searchEntry);
    }

    private void buildManySearchEntries(int numberToCreate) {
        Calendar calendar = Calendar.getInstance();
        for (int number = 1; number <= numberToCreate; number++) {
            calendar.set(2012, 10, number);

            SearchEntry entry = SearchEntryBuilder.entry()
                    .title("This week in Spring - November " + number + ", 2012")
                    .summary("Html summary")
                    .rawContent("Raw Content")
                    .path("/blog/" + number)
                    .blog();

            searchService.saveToIndex(entry);
        }
    }
}
