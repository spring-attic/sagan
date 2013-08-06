package integration.redirector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.redirector.RedirectMappingService;
import org.springframework.redirector.RedirectorConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RedirectControllerTests.Configuration.class)
public class RedirectControllerTests {

	@Import(RedirectorConfiguration.class)
	public static class Configuration {
		@Bean
		public RedirectMappingService redirectMappingService() {
			Map<String, String> mappings = new HashMap<>();
			mappings.put("/old", "http://example.com/new");
			return new RedirectMappingService(mappings);
		}
	}

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void redirectsWhenUrlIsFound() throws Exception {
		this.mockMvc.perform(get("/old"))
				.andExpect(status().isMovedPermanently())
				.andExpect(header().string("Location", "http://example.com/new"));
	}


	@Test
	public void redirectsPreservingQueryString() throws Exception {
		this.mockMvc.perform(get("/old?foo=bar"))
				.andExpect(status().isMovedPermanently())
				.andExpect(header().string("Location", "http://example.com/new?foo=bar"));
	}

}
