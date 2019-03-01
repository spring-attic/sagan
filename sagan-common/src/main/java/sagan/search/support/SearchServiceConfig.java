package sagan.search.support;

import sagan.SaganProfiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.searchbox.client.JestClient;

import com.google.gson.Gson;


@Configuration
@Profile(SaganProfiles.JEST)
class JestSearchServiceConfig {

    @Bean
    public SearchResultParser searchResultParser() {
        return new SearchResultParser();
    }

    @Bean
    public JestSearchService searchService(JestClient jestClient, SearchResultParser searchResultParser, Gson gson) {
        return new JestSearchService(jestClient, searchResultParser, gson);
    }
}
