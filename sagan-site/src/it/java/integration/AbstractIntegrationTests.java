package integration;

import sagan.search.service.InMemoryElasticSearchConfiguration;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import integration.configuration.IntegrationTestsConfiguration;
import integration.stubs.StubGithubRestClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static requestpostprocessors.SecurityRequestPostProcessors.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { IntegrationTestsConfiguration.class, InMemoryElasticSearchConfiguration.class },
                      initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public abstract class AbstractIntegrationTests {

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
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(springSecurityFilterChain)
                .defaultRequest(get("/").with(csrf()).with(user(123L).roles("ADMIN")))
                .build();
    }

    @After
    public void clearCaches() throws Exception {
        for (String name : cacheManager.getCacheNames()) {
            cacheManager.getCache(name).clear();
        }
    }
}
