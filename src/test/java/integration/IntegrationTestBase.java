package integration;

import integration.configuration.IntegrationTestsConfiguration;
import integration.stubs.StubGithubRestClient;
import io.spring.site.search.configuration.InMemoryElasticSearchConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static requestpostprocessors.SecurityRequestPostProcessors.csrf;
import static requestpostprocessors.SecurityRequestPostProcessors.user;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {IntegrationTestsConfiguration.class,
        InMemoryElasticSearchConfiguration.class},
        initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public abstract class IntegrationTestBase {

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected StubGithubRestClient stubRestClient;

    @Autowired
    protected FilterChainProxy springSecurityFilterChain;

    protected MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void setupMockMvc() {
        stubRestClient.clearResponses();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilters(springSecurityFilterChain)
                .defaultRequest(get("/").with(csrf()).with(user(123L).roles("ADMIN")))
                .build();
    }

    @After
    public void tearDown() throws Exception {
        for (String name : this.cacheManager.getCacheNames()) {
            this.cacheManager.getCache(name).clear();
        }
    }
}
