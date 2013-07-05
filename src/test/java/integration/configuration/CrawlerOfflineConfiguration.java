package integration.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.index.configuration.CrawlerConfiguration;

@Configuration
@Import({CrawlerConfiguration.class, ElasticsearchStubbedConfiguration.class})
public class CrawlerOfflineConfiguration {

}
