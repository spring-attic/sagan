package sagan.search;

import java.util.LinkedHashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;

@Configuration
public class SearchClientConfig {

    private static Log logger = LogFactory.getLog(SearchClientConfig.class);

    @Value("${elasticsearch.client.endpoint}")
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
        logger.info("**** Elastic Search endpoint: " + endpoint);
        return new ClientConfig.Builder(servers).multiThreaded(true).build();
    }

}
