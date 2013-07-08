package org.springframework.search.configuration;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;

public class LocalHostElasticSearchConfiguration {

	@Value("${elasticsearch.cluster.nodes:localhost:9300}")
	private String clusterNodes;

	@Value("${elasticsearch.cluster.name:elasticsearch}")
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
