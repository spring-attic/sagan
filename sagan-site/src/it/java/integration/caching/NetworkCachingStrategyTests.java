package integration.caching;

import integration.caching.NetworkCachingStrategyTests.TestConfiguration;
import io.spring.site.web.configuration.ApplicationConfiguration;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.social.github.api.GitHub;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import sagan.util.FixtureLoader;
import sagan.util.SetSystemProperty;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfiguration.class }, initializers = ConfigFileApplicationContextInitializer.class)
public class NetworkCachingStrategyTests {

    @ClassRule
    public static SetSystemProperty timeToLive = new SetSystemProperty(
            "cache.network.timetolive", "1");

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;
    private RestOperations restOperations;

    @Before
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Configuration
    @Import({ ApplicationConfiguration.class })
    public static class TestConfiguration {
        @Bean
        @Primary
        public GitHub gitHub() {
            return mock(GitHub.class);
        }

        @Bean
        @Primary
        public RestTemplate restTemplate() {
            return mock(RestTemplate.class);
        }
    }

    @Autowired
    private GitHub gitHub;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        reset(this.gitHub);
        reset(this.restTemplate);

        String requestPath = "https://api.github.com/orgs/spring-guides/repos";
        String repoList = FixtureLoader.load("/fixtures/github/githubRepoList.json");

        this.restOperations = mock(RestOperations.class);
        given(this.gitHub.restOperations()).willReturn(this.restOperations);
        given(
                this.restOperations.getForObject(startsWith(requestPath),
                        (Class<String>) anyObject())).willReturn(repoList);
    }

    @After
    public void tearDown() throws Exception {
        for (String name : this.cacheManager.getCacheNames()) {
            this.cacheManager.getCache(name).clear();
        }
    }

    @Test
    public void githubRequestsAreCached() throws Exception {
        this.mockMvc.perform(get("/guides")).andExpect(status().isOk());
        this.mockMvc.perform(get("/guides")).andExpect(status().isOk());
        verify(this.restOperations, times(1)).getForObject(anyString(),
                (Class<?>) anyObject());
    }

    @Test
    public void cachedItemsHaveATimeToLive() throws Exception {
        this.mockMvc.perform(get("/guides")).andExpect(status().isOk());
        verify(this.restOperations).getForObject(anyString(), (Class<?>) anyObject());

        Thread.sleep(1500);

        this.mockMvc.perform(get("/guides")).andExpect(status().isOk());
        verify(this.restOperations, times(2)).getForObject(anyString(),
                (Class<?>) anyObject());
    }

    @Test
    public void toolsSTSXmlRequestsAreCached() throws Exception {
        String stsDownloads = FixtureLoader.load("/fixtures/tools/sts_downloads.xml");
        given(
                this.restTemplate.getForObject(
                        "http://dist.springsource.com/release/STS/index-new.xml",
                        String.class)).willReturn(stsDownloads);

        this.mockMvc.perform(get("/tools")).andExpect(status().isOk());
        this.mockMvc.perform(get("/tools")).andExpect(status().isOk());

        verify(this.restTemplate, times(1)).getForObject(anyString(),
                (Class<?>) anyObject());
    }

    @Test
    public void toolsEclipseXmlRequestsAreCached() throws Exception {
        String eclipse = FixtureLoader.load("/fixtures/tools/eclipse.xml");
        given(
                this.restTemplate.getForObject(
                        "http://download.springsource.com/release/STS/eclipse.xml",
                        String.class)).willReturn(eclipse);

        this.mockMvc.perform(get("/tools/eclipse")).andExpect(status().isOk());
        this.mockMvc.perform(get("/tools/eclipse")).andExpect(status().isOk());

        verify(this.restTemplate, times(1)).getForObject(anyString(),
                (Class<?>) anyObject());
    }

}
