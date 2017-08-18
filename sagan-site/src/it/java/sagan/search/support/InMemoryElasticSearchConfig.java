package sagan.search.support;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
class InMemoryElasticSearchConfig {

    private static final Logger log = LoggerFactory.getLogger(InMemoryElasticSearchConfig.class);

    @Autowired
    private SearchService searchService;

    private Client client;

    private Node node;

    @PostConstruct
    public void configureSearchService() {
        searchService.setUseRefresh(true);
        new Thread(this::start).start();
    }

    private void start() {
        log.info("Starting Elastic Search");
        NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder().local(true);
        nodeBuilder.getSettings().put("network.host", "127.0.0.1");
        node = nodeBuilder.node();
        client = node.client();
        log.info("Started Elastic Search");
    }

    @PreDestroy
    public void shutDownElasticSearch() throws Exception {
        if (node != null) {
            try {
                client.close();
                node.close();
            } catch (Throwable e) {
                log.warn("Failed to shutdown Elastic Search");
            }
        }
    }
}
