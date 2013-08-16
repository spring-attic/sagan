package integration.configuration;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.site.search.SearchService;
import utils.FreePortFinder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.LinkedHashSet;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@Configuration
public class InMemoryElasticSearchConfiguration {

	@Autowired
	private SearchService searchService;

	@Autowired
	private Client client;

	private String elasticSearchPort;

	@Bean
	public Client elasticSearchClient() throws Exception {
		NodeBuilder nodeBuilder = nodeBuilder().local(false);
		nodeBuilder.getSettings().put("network.host", "127.0.0.1");
		nodeBuilder.getSettings().put("http.port", getElasticSearchPort());
		nodeBuilder.getSettings().put("index.number_of_shards", "1");
		nodeBuilder.getSettings().put("index.number_of_replicas", "0");
		Client client = nodeBuilder.node().client();
		return client;
	}

	private String getElasticSearchPort() {
		if (this.elasticSearchPort == null) {
			this.elasticSearchPort = FreePortFinder.find() + "";
		}
		return this.elasticSearchPort;
	}

	@PostConstruct
	public void configureSearchService() {
		this.searchService.setUseRefresh(true);
	}

	@PreDestroy
	public void closeClient() throws Exception {
		this.client.close();
	}

	@Bean
	@Primary
	public JestClient jestClient() {
		JestClientFactory factory = new JestClientFactory();
		factory.setClientConfig(clientConfig());
		return factory.getObject();
	}

	private ClientConfig clientConfig() {
		LinkedHashSet<String> servers = new LinkedHashSet<>();
		servers.add("http://localhost:" + getElasticSearchPort());
		ClientConfig clientConfig = new ClientConfig.Builder(servers).multiThreaded(true).build();
		return clientConfig;
	}
}
