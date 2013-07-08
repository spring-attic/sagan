package org.springframework.search.configuration;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.search.SearchEntry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class InMemoryElasticSearchConfiguration {

	@Autowired
	private Client client;

	@PostConstruct
	public void deleteSearchIndex() throws Exception {
		ElasticsearchTemplate elasticsearchTemplate = new ElasticsearchTemplate(client);
		elasticsearchTemplate.deleteIndex(SearchEntry.class);
		elasticsearchTemplate.createIndex(SearchEntry.class);
		elasticsearchTemplate.putMapping(SearchEntry.class);
		elasticsearchTemplate.refresh(SearchEntry.class, false);
	}

	@PreDestroy
	public void closeClient() throws Exception {
		client.close();
	}

	@Bean
	@Singleton
	public Client elasticSearchClient() throws Exception {
		NodeBuilder nodeBuilder = nodeBuilder().local(false);
		nodeBuilder.getSettings().put("network.host", "127.0.0.1");
		Client client = nodeBuilder.node().client();
		return client;
	}
}
