package org.springframework.search.configuration;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;

public class HostedElasticSearchConfiguration {

	@Value("${elasticsearch.cluster.nodes:ec2-50-112-200-3.us-west-2.compute.amazonaws.com:9300}")
	private String clusterNodes = "ec2-50-112-200-3.us-west-2.compute.amazonaws.com:9300";

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
