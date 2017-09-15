package sagan.support.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import sagan.SiteApplication;
import sagan.support.Fixtures;
import sagan.support.SetSystemProperty;
import sagan.support.cache.CachedRestClientTests.TestConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.social.github.api.GitHub;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

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

/**
 * Integration tests ensuring that caching functionality works as expected in
 * {@link CachedRestClient}.
 */
@Ignore // see #342
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
public class CachedRestClientTests {

	@ClassRule
	public static SetSystemProperty timeToLive = new SetSystemProperty(CachedRestClient.CACHE_TTL_KEY, "1");

	@Autowired
	protected WebApplicationContext wac;

	protected MockMvc mockMvc;

	private RestOperations restOperations;

	@Before
	public void setupMockMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	/**
	 * Not marked with @Configuration so as to avoid accidental component-scanning by
	 * {@link SiteApplication} in other tests.
	 */
	@Import(SiteApplication.class)
	public static class TestConfig {
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
		reset(gitHub);
		reset(restTemplate);

		String requestPath = "https://api.github.com/orgs/spring-guides/repos";

		restOperations = mock(RestOperations.class);
		given(gitHub.restOperations()).willReturn(restOperations);
		given(restOperations.getForObject(
				startsWith(requestPath), (Class<String>) anyObject())).willReturn(Fixtures.githubRepoListJson());
	}

	@After
	public void tearDown() throws Exception {
		for (String name : cacheManager.getCacheNames()) {
			cacheManager.getCache(name).clear();
		}
	}

	@Test
	public void githubRequestsAreCached() throws Exception {
		mockMvc.perform(get("/guides")).andExpect(status().isOk());
		mockMvc.perform(get("/guides")).andExpect(status().isOk());
		verify(restOperations, times(1)).getForObject(anyString(), (Class<?>) anyObject());
	}

	@Test
	public void cachedItemsHaveATimeToLive() throws Exception {
		mockMvc.perform(get("/guides")).andExpect(status().isOk());
		verify(restOperations).getForObject(anyString(), (Class<?>) anyObject());

		Thread.sleep(1500);

		mockMvc.perform(get("/guides")).andExpect(status().isOk());
		verify(restOperations, times(2)).getForObject(anyString(), (Class<?>) anyObject());
	}

	@Test
	public void toolsSTSXmlRequestsAreCached() throws Exception {
		String stsDownloads = Fixtures.load("/fixtures/tools/sts_downloads.xml");
		given(
				restTemplate.getForObject(
						"http://dist.springsource.com/release/STS/index-new.xml",
						String.class)).willReturn(stsDownloads);

		mockMvc.perform(get("/tools")).andExpect(status().isOk());
		mockMvc.perform(get("/tools")).andExpect(status().isOk());

		verify(restTemplate, times(1)).getForObject(anyString(), (Class<?>) anyObject());
	}

	@Test
	public void toolsEclipseXmlRequestsAreCached() throws Exception {
		String eclipse = Fixtures.load("/fixtures/tools/eclipse.xml");
		given(
				restTemplate.getForObject(
						"http://dist.springsource.com/release/STS/eclipse.xml",
						String.class)).willReturn(eclipse);

		mockMvc.perform(get("/tools/eclipse")).andExpect(status().isOk());
		mockMvc.perform(get("/tools/eclipse")).andExpect(status().isOk());

		verify(restTemplate, times(1)).getForObject(anyString(), (Class<?>) anyObject());
	}

}
