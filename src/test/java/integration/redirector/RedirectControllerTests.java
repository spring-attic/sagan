package integration.redirector;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.redirector.RedirectorConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RedirectorConfiguration.class)

public class RedirectControllerTests {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private static String originalMappingsPath;

	@BeforeClass
	public static void beforeAll() {
		originalMappingsPath = System.getProperty("redirectMappings.path");
		System.setProperty("redirectMappings.path", "classpath:test-mappings.yml");
	}

	@AfterClass
	public static void afterAll() {
		if (originalMappingsPath == null) {
			System.clearProperty("redirectMappings.path");
		} else {
			System.setProperty("redirectMappings.path", originalMappingsPath);
		}
	}

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

}
