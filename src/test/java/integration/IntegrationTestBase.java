package integration;

import integration.configuration.IntegrationTestsConfiguration;
import org.junit.runner.RunWith;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IntegrationTestsConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public abstract class IntegrationTestBase {
}
