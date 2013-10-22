package integration;

import sagan.app.indexer.ApplicationConfiguration;
import sagan.util.SetSystemProperty;

import org.junit.ClassRule;
import org.junit.runner.RunWith;

import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static integration.IndexerIntegrationTestBase.TestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfiguration.class },
                      initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public abstract class IndexerIntegrationTestBase {

    @ClassRule
    public static SetSystemProperty delay = new SetSystemProperty("search.indexer.delay", "60000000");

    @Configuration
    @Import({ ApplicationConfiguration.class })
    public static class TestConfiguration {

    }
}
