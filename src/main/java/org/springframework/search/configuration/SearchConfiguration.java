package org.springframework.search.configuration;

import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

@Configuration
public class SearchConfiguration {

	@Bean
	public ElasticsearchOperations elasticsearchTemplate(Client client) throws Exception {
		return new ElasticsearchTemplate(client);
	}

}
