package org.springframework.site.configuration;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.site.search.SearchEntry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@Configuration
public class ElasticSearchConfiguration {

	@Autowired
	private Client client;

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() throws Exception {
		return new ElasticsearchTemplate(client);
	}

	@Configuration
	@Profile({"development", "staging", "production"})
	protected static class ExternalElasticSearchConfiguration {

		@Value("${elasticsearch.cluster.nodes:ec2-50-112-200-3.us-west-2.compute.amazonaws.com:9300}")
		private String clusterNodes = "localhost:9300";

		@Value("${elasticsearch.cluster.name:elasticsearch}")
		private String clusterName = "elasticsearch";

		@Bean
		public Client elasticSearchClient() throws Exception {
			TransportClientFactoryBean transportClient = new TransportClientFactoryBean();
			transportClient.setClusterNodes(clusterNodes);
			transportClient.setClusterName(clusterName);
			transportClient.setClientTransportSniff(false);
			transportClient.afterPropertiesSet();
			return transportClient.getObject();
		}
	}

	@Configuration
	@Profile({"default"})
	protected static class LocalElasticSearchConfiguration {

		@Value("${elasticsearch.cluster.nodes:localhost:9300}")
		private String clusterNodes;

		@Value("${elasticsearch.cluster.name:elasticsearch_pivotal}")
		private String clusterName;

		@Bean
		public Client elasticSearchClient() throws Exception {
			TransportClientFactoryBean transportClient = new TransportClientFactoryBean();
			transportClient.setClusterNodes(clusterNodes);
			transportClient.setClusterName(clusterName);
			transportClient.setClientTransportSniff(false);
			transportClient.afterPropertiesSet();
			return transportClient.getObject();
		}
	}

	@Configuration
	@Profile({"memory"})
	protected static class InMemoryElasticSearchConfiguration {

		@PostConstruct
		public void deleteSearchIndex() throws Exception {
			ElasticsearchTemplate elasticsearchTemplate = new ElasticsearchTemplate(elasticSearchClient());
			elasticsearchTemplate.deleteIndex(SearchEntry.class);
			elasticsearchTemplate.createIndex(SearchEntry.class);
			elasticsearchTemplate.putMapping(SearchEntry.class);
			elasticsearchTemplate.refresh(SearchEntry.class, false);
		}

		@PreDestroy
		public void closeClient() throws Exception {
			elasticSearchClient().close();
		}

		@Bean
		public Client elasticSearchClient() throws Exception {
			return nodeBuilder().local(true).node().client();
		}
	}

}
