package sagan.search.support;

import sagan.search.support.SearchService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class InMemoryElasticSearchConfig {

    @Autowired
    private SearchService searchService;

    @Autowired
    private Client client;

    private Node node;

    @Bean
    public Client elasticSearchClient() throws Exception {
        NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder().local(true);
        nodeBuilder.getSettings().put("network.host", "127.0.0.1");
        node = nodeBuilder.node();
        Client client = node.client();
        return client;
    }

    @PostConstruct
    public void configureSearchService() {
        searchService.setUseRefresh(true);
    }

    @PreDestroy
    public void shutDownElasticSearch() throws Exception {
        client.close();
        node.close();
    }
}
