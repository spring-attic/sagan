package sagan.search;

import sagan.search.remote.RemoteSearchService;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class RemoteSearchServiceConfig {

    @Bean
    public RemoteSearchService searchService(RestTemplateBuilder builder) {
        return new RemoteSearchService(builder.build());
    }
}
