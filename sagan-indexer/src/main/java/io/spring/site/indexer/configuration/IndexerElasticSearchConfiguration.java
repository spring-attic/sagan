package io.spring.site.indexer.configuration;

import sagan.search.service.InMemoryElasticSearchConfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class IndexerElasticSearchConfiguration {

    @Configuration
    @Profile({"default"})
    protected static class IndexerInMemoryConfiguration extends InMemoryElasticSearchConfiguration {}

}
