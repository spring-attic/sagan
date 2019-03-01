package sagan.search.web;

import sagan.search.service.SearchQuery;
import sagan.search.service.support.ElasticSearchService;
import sagan.search.types.GuideDoc;
import sagan.search.types.SearchEntry;

import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import saganx.AbstractIntegrationTests;

public class SearchServiceControllerTests extends AbstractIntegrationTests {

    @MockBean
    private ElasticSearchService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void search() throws Exception {
        SearchQuery value = new SearchQuery("foo");
        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(
                value))).andExpect(status().is2xxSuccessful());
        verify(service).search(argThat(equalTo("foo")), any(Pageable.class), argThat(new EmptyList()));
    }

    @Test
    public void searchPage() throws Exception {
        SearchQuery value = new SearchQuery("foo");
        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(
                value)).param("page", "2")).andExpect(status().is2xxSuccessful());
        verify(service).search(argThat(equalTo("foo")), argThat(new PageableMatcher(2)), argThat(new EmptyList()));
    }

    @Test
    public void deleteSearchEntry() throws Exception {
        mockMvc.perform(delete("/index/abcdef/gs/stuff")).andExpect(status().is2xxSuccessful());
        verify(service).removeFromIndex(argThat(new SearchEntryMatcher("abcdef", "gs/stuff")));
    }

    @Test
    public void createSearchEntry() throws Exception {
        GuideDoc value = new GuideDoc();
        value.setPath("gs/stuff");
        mockMvc.perform(post("/index").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(
                value))).andExpect(status().is2xxSuccessful());
        verify(service).saveToIndex(argThat(new SearchEntryMatcher("guidedoc", "gs/stuff")));
    }

    @Test
    public void deleteProjectVersion() throws Exception {
        mockMvc.perform(delete("/projects/abcdef/1.0.0.RELEASE")).andExpect(status().is2xxSuccessful());
        verify(service).removeOldProjectEntriesFromIndex(argThat(equalTo("abcdef")), argThat(new ListMatcher(
                "1.0.0.RELEASE")));
    }

    private class SearchEntryMatcher extends ArgumentMatcher<SearchEntry> {
        private String type;
        private String path;

        public SearchEntryMatcher(String type, String path) {
            this.type = type;
            this.path = path;
        }

        @Override
        public boolean matches(Object argument) {
            SearchEntry entry = (SearchEntry) argument;
            return entry.getPath().equals(path) && entry.getType().equals(type);
        }
    }

    private class ListMatcher extends ArgumentMatcher<List<String>> {

        private String item;

        public ListMatcher(String item) {
            this.item = item;
        }

        @Override
        public boolean matches(Object argument) {
            @SuppressWarnings("unchecked")
            List<String> entry = (List<String>) argument;
            return entry.contains(item);
        }
    }

    private class PageableMatcher extends ArgumentMatcher<Pageable> {

        private int page;

        public PageableMatcher(int page) {
            this.page = page;
        }

        @Override
        public boolean matches(Object argument) {
            Pageable entry = (Pageable) argument;
            return entry.getPageNumber() == page;
        }
    }

    private class EmptyList extends ArgumentMatcher<List<String>> {

        @Override
        public boolean matches(Object argument) {
            @SuppressWarnings("unchecked")
            List<String> entry = (List<String>) argument;
            return entry.isEmpty();
        }
    }
}
