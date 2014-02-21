package sagan;

import sagan.util.FreePortFinder;

import org.junit.After;
import org.junit.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.Assert.assertNotNull;

public class IndexerConfigTests {

    private ConfigurableApplicationContext context;

    @After
    public void clean() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void testContextLoading() throws Exception {
        int port = FreePortFinder.find();

        context = SpringApplication.run(IndexerConfig.class,
                "--server.port=" + port,
                "--spring.database.url=jdbc:h2:mem:acceptancetestdb;MODE=PostgreSQL",
                "--search.indexer.delay=6000000",
                "--elasticsearch.client.endpoint=http://localhost:9200",
                "--elasticsearch.client.index=sagan-test",
                "--spring.profiles.active=acceptance");

        IndexerConfig configuration = context.getBean(IndexerConfig.class);
        assertNotNull(configuration);
        context.close();
    }

}
