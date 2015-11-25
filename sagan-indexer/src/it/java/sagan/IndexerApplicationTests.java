package sagan;

import sagan.support.FreePortFinder;

import org.junit.After;
import org.junit.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.Assert.assertNotNull;

public class IndexerApplicationTests {

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

        context = new SaganApplication(IndexerApplication.class).run(
                "--server.port=" + port,
                "--spring.database.url=jdbc:h2:mem:sagan;MODE=PostgreSQL",
                "--search.indexer.delay=6000000",
                "--elasticsearch.client.endpoint=http://localhost:9200",
                "--elasticsearch.client.index=sagan-test");

        IndexerApplication configuration = context.getBean(IndexerApplication.class);
        assertNotNull(configuration);
        context.close();
    }

}
