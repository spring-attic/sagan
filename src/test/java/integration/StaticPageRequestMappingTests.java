package integration;

import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StaticPageRequestMappingTests extends IntegrationTestBase {

	@Test
	public void getHomePage() throws Exception {
		checkPage("/");
	}

	@Test
	public void getAboutPage() throws Exception {
		checkPage("/about");
	}

	@Test
	public void getJobsPage() throws Exception {
		checkPage("/jobs");
	}

	@Test
	public void getServicesPage() throws Exception {
		checkPage("/services");
	}

	@Test
	public void getSigninPage() throws Exception {
		checkPage("/signin");
	}

	@Test
	public void getStylePage() throws Exception {
		checkPage("/style");
	}

	@Test
	public void getTrademarkPage() throws Exception {
		checkPage("/trademark");
	}

	private void checkPage(String page) throws Exception {
		this.mockMvc.perform(get(page))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"));
	}

	@Test
	public void getAStaticPageWithSlashAtEnd() throws Exception {
		checkPage("/about/");
	}

	@Test
	public void getRobotsFile() throws Exception {
		this.mockMvc.perform(get("/robots.txt"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("User-agent")));
	}

	@Test
	public void doesNotGetIndexPage() throws Exception {
		this.mockMvc.perform(get("/index"))
				.andExpect(status().isNotFound());
	}

}
