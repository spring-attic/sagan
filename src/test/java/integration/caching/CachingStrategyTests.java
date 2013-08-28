package integration.caching;

import io.spring.site.test.FixtureLoader;
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
import org.springframework.web.context.WebApplicationContext;
import utils.SetSystemProperty;

import static integration.caching.CachingStrategyTests.TestConfiguration;
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
@ContextConfiguration(classes = {TestConfiguration.class},
		initializers = ConfigFileApplicationContextInitializer.class)
public class CachingStrategyTests {

	@ClassRule
	public static SetSystemProperty timeToLive = new SetSystemProperty("cache.timetolive", "1");

	@Autowired
	protected WebApplicationContext wac;

	protected MockMvc mockMvc;
	private RestOperations restOperations;

	@Before
	public void setupMockMvc() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Configuration
	@Import({ApplicationConfiguration.class})
	public static class TestConfiguration {
		@Bean
		@Primary
		public GitHub gitHub(){
			return mock(GitHub.class);
		}
	}

	@Autowired
	private GitHub gitHub;

	@Autowired
	private CacheManager cacheManager;

	@Before
	public void setUp() throws Exception {
		reset(gitHub);
		String requestPath = "https://api.github.com/orgs/spring-guides/repos";
		String repoList = FixtureLoader.load("/fixtures/github/githubRepoList.json");

		restOperations = mock(RestOperations.class);
		given(gitHub.restOperations()).willReturn(restOperations);
		given(restOperations.getForObject(
				startsWith(requestPath),
				(Class<String>) anyObject())).willReturn(repoList);
	}

	@After
	public void tearDown() throws Exception {
		for (String name : cacheManager.getCacheNames()) {
			cacheManager.getCache(name).clear();
		}
	}

	@Test
	public void githubRequestsAreCached() throws Exception {
		this.mockMvc.perform(get("/guides")).andExpect(status().isOk());
		this.mockMvc.perform(get("/guides")).andExpect(status().isOk());
		verify(restOperations, times(1)).getForObject(anyString(), (Class<?>) anyObject());
	}

	@Test
	public void cachedItemsHaveATimeToLive() throws Exception {
		this.mockMvc.perform(get("/guides")).andExpect(status().isOk());
		verify(restOperations).getForObject(anyString(), (Class<?>) anyObject());

		Thread.sleep(1500);

		this.mockMvc.perform(get("/guides")).andExpect(status().isOk());
		verify(restOperations, times(2)).getForObject(anyString(), (Class<?>) anyObject());
	}

}
