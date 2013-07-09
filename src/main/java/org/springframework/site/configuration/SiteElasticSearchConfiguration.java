package org.springframework.site.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.search.configuration.SearchConfiguration;
import org.springframework.search.configuration.HostedElasticSearchConfiguration;
import org.springframework.search.configuration.LocalHostElasticSearchConfiguration;

@Configuration
@Import({SearchConfiguration.class})
public class SiteElasticSearchConfiguration {

	@Configuration
	@Profile({"default", "local"})
	protected static class SiteLocalConfiguration extends LocalHostElasticSearchConfiguration {}

	@Configuration
	@Profile({"staging"})
	protected static class SiteStagingConfiguration extends HostedElasticSearchConfiguration {}

}
