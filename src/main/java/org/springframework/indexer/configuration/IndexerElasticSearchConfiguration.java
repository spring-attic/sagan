package org.springframework.indexer.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.search.configuration.InMemoryElasticSearchConfiguration;

@Configuration
public class IndexerElasticSearchConfiguration {

	@Configuration
	@Profile({"default"})
	protected static class IndexerInMemoryConfiguration extends InMemoryElasticSearchConfiguration {}

}
