package sagan.questions.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import saganx.AbstractIntegrationTests;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionsIndexTests extends AbstractIntegrationTests {

	@Test
	public void showsQuestionsIndex() throws Exception {
		MvcResult result = mockMvc.perform(get("/questions")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html")).andReturn();

		Document document = Jsoup.parse(result.getResponse().getContentAsString());

		assertThat(document.select("body").text(), containsString("QUESTIONS"));
	}


}
