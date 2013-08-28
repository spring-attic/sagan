package integration.caching;

import io.spring.site.test.FixtureLoader;
import io.spring.site.web.configuration.ApplicationConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfiguration.class},
		initializers = ConfigFileApplicationContextInitializer.class)
public class CachingStrategyTests {

	@Autowired
	protected WebApplicationContext wac;

	protected MockMvc mockMvc;

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

	@Before
	public void setUp() throws Exception {
		reset(gitHub);
	}

	@Test
	public void githubRequestsAreCached() throws Exception {
		String requestPath = "https://api.github.com/orgs/spring-guides/repos";
		String repoList = FixtureLoader.load("/fixtures/github/githubRepoList.json");

		RestOperations restOperations = mock(RestOperations.class);
		given(gitHub.restOperations()).willReturn(restOperations);
		given(restOperations.getForObject(
				startsWith(requestPath),
				(Class<String>) anyObject())).willReturn(repoList);

		this.mockMvc.perform(get("/guides"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		this.mockMvc.perform(get("/guides"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		verify(restOperations, times(1)).getForObject(anyString(), (Class<?>) anyObject());
	}

}
