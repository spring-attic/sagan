package org.springframework.site.search.configuration;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashSet;

@Configuration
public class SearchClientConfiguration {

	@Value("${elasticsearch.client.endpoint:http://localhost:9200}")
	private String endpoint;

	@Bean
	public JestClient jestClient() {
		JestClientFactory factory = new JestClientFactory();
		factory.setClientConfig(clientConfig());
		return factory.getObject();
	}

	private ClientConfig clientConfig() {


		LinkedHashSet<String> servers = new LinkedHashSet<>();
		servers.add(endpoint);
		return new ClientConfig.Builder(servers).multiThreaded(true).build();
	}

}
