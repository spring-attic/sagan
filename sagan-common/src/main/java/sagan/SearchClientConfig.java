package sagan;

import java.util.LinkedHashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;

@Configuration
class SearchClientConfig {

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
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
        return new ClientConfig.Builder(servers).multiThreaded(true).gson(gson).build();
    }

}
