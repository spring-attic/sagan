package integration.search;

import integration.IntegrationTestBase;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SearchPageTests extends IntegrationTestBase{

	@Test
	public void search_rendersTheResults() throws Exception {
		this.mockMvc.perform(get("/search?q=somesearch"))
				.andExpect(status().isOk());
	}
}
