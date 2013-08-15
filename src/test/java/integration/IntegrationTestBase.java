package integration;

import integration.configuration.IntegrationTestsConfiguration;
import integration.stubs.StubGithubRestClient;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IntegrationTestsConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public abstract class IntegrationTestBase {

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	protected StubGithubRestClient stubRestClient;

	protected MockMvc mockMvc;

	@Before
	public void setupMockMvc() {
		stubRestClient.clearResponses();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
}
