package integration.guides;

import integration.IntegrationTestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GuidesTests extends IntegrationTestBase {

	@Test
	public void showsToolsIndex() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/guides"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document document = Jsoup.parse(result.getResponse().getContentAsString());
		assertThat(document.select("ul li.active").text(), equalTo("Guides"));
	}
}
