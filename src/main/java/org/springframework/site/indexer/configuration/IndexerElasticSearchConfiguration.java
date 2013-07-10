package org.springframework.site.indexer.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.site.search.configuration.InMemoryElasticSearchConfiguration;

@Configuration
public class IndexerElasticSearchConfiguration {

	@Configuration
	@Profile({"default"})
	protected static class IndexerInMemoryConfiguration extends InMemoryElasticSearchConfiguration {}

}
