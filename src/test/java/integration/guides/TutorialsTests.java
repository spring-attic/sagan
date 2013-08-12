package integration.guides;

import integration.IntegrationTestBase;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TutorialsTests extends IntegrationTestBase {

	@Test
	public void getTutorialRootPage() throws Exception {
		this.mockMvc.perform(get("/guides/tutorials/my-tutorial"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"));

	}

	@Test
	public void getTutorialPage1() throws Exception {
		this.mockMvc.perform(get("/guides/tutorials/my-tutorial/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"));

	}

	@Test
	public void getImage() throws Exception {
		this.mockMvc.perform(get("/guides/tutorials/my-tutorial/images/image.png"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("image/png"));

	}
}
