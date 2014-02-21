package sagan.app.indexer;

import sagan.IndexerConfig;
import sagan.util.SetSystemProperty;

import org.junit.ClassRule;
import org.junit.runner.RunWith;

import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IndexerConfig.class,
                      initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public abstract class AbstractIndexerIntegrationTests {

    @ClassRule
    public static SetSystemProperty delay = new SetSystemProperty("search.indexer.delay", "60000000");

}
