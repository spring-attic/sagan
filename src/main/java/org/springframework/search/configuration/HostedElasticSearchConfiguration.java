package org.springframework.search.configuration;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashSet;

public class HostedElasticSearchConfiguration {

	@Value("${elasticsearch.cluster.nodes:ec2-50-112-200-3.us-west-2.compute.amazonaws.com:9300}")
	private String clusterNodes = "ec2-50-112-200-3.us-west-2.compute.amazonaws.com:9300";

	@Bean
	public ClientConfig clientConfig() {
		ClientConfig clientConfig = new ClientConfig();
		LinkedHashSet<String> servers = new LinkedHashSet<String>();
		for (String url : this.clusterNodes.split(",")) {
			if (!url.startsWith("http")) {
				url = "http://" + url;
			}
			if (url.endsWith("9300")) {
				url = url.replace("9300", "9200");
			}
			servers.add(url);
		}
		clientConfig.getProperties().put(ClientConstants.SERVER_LIST, servers);
		clientConfig.getProperties().put(ClientConstants.IS_MULTI_THREADED, true);
		return clientConfig;
	}

	@Bean
	public JestClient jestClient() {
		JestClientFactory factory = new JestClientFactory();
		factory.setClientConfig(clientConfig());
		return factory.getObject();
	}
}
