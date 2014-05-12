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
        String body = document.select("body").text();

        // questions
        assertThat(body, containsString("QUESTIONS"));

        // see seed data in sagan-common/src/main/resources/database/V4__stackoverflow_tags.sql
        assertThat(body, containsString("Spring Framework"));
        assertThat(body, containsString("spring-framework"));
        assertThat(body, containsString("spring-core"));
        assertThat(body, containsString("dependency-injection"));
        assertThat(body, containsString("Spring Data"));
        assertThat(body, containsString("spring-data"));
        assertThat(body, containsString("spring-data-mongodb"));
        assertThat(body, containsString("spring-data-neo4j"));
    }
}
