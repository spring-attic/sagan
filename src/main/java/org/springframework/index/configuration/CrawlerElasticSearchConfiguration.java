package org.springframework.index.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.search.configuration.HostedElasticSearchConfiguration;
import org.springframework.search.configuration.InMemoryElasticSearchConfiguration;

@Configuration
public class CrawlerElasticSearchConfiguration {

	@Configuration
	@Profile({"default", "local"})
	protected static class CrawlerInMemoryConfiguration extends InMemoryElasticSearchConfiguration {}

	@Configuration
	@Profile({"staging"})
	protected static class CrawlerStagingConfiguration extends HostedElasticSearchConfiguration {}


}
