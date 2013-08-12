package integration.admin;

import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminDashboardTests extends IntegrationTestBase {
	private Principal principal;

	@Before
	public void setup() {
		principal = new Principal() {
			@Override
			public String getName() {
				return "admin";
			}
		};
	}

	@Test
	public void adminDashboard() throws Exception {
		mockMvc.perform(get("/admin").principal(principal)).andExpect(status().isOk());
	}
}
