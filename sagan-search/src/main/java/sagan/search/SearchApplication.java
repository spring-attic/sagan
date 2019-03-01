package sagan.search;

import sagan.SaganApplication;
import sagan.search.service.ElasticSearchService;
import sagan.support.health.ElasticsearchHealthIndicator;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.searchbox.client.JestClient;

/**
 * The entry point for the Sagan search application.
 */
@SpringBootApplication
public class SearchApplication {


    public static void main(String[] args) {
        new SaganApplication(SearchApplication.class).run(args);
    }

    @Bean
    public ElasticsearchHealthIndicator elasticsearch(JestClient jestClient, ElasticSearchService searchService) {
        return new ElasticsearchHealthIndicator(jestClient, searchService.getIndexName());
    }

}
