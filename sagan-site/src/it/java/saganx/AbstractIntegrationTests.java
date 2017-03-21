package saganx;

import sagan.SaganProfiles;
import sagan.blog.support.BlogService;
import sagan.support.github.StubGithubRestClient;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cache.CacheManager;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@ContextConfiguration(classes = { IntegrationTestsConfig.class })
@Transactional
@ActiveProfiles(profiles = { SaganProfiles.STANDALONE })
@AutoConfigureMockMvc(addFilters = false)
public abstract class AbstractIntegrationTests {

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected StubGithubRestClient stubRestClient;

    @Autowired
    protected BlogService blogService;

    @Autowired
    protected FilterChainProxy springSecurityFilterChain;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void setupMockMvc() {
        stubRestClient.clearResponses();
    }

    @After
    public void clearCaches() throws Exception {
        for (String name : cacheManager.getCacheNames()) {
            cacheManager.getCache(name).clear();
        }
    }
}
