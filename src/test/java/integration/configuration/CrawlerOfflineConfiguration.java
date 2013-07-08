package integration.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.indexer.configuration.IndexerConfiguration;

@Configuration
@Import({IndexerConfiguration.class, ElasticsearchStubbedConfiguration.class})
public class CrawlerOfflineConfiguration {

}
