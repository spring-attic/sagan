package sagan.guides.support;

import sagan.support.Fixtures;
import saganx.AbstractIntegrationTests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UnderstandingDocIntegrationTests extends AbstractIntegrationTests {

    @Test
    public void getExistingGuide() throws Exception {
        String readmeHtml = Fixtures.load("/fixtures/understanding/amqp/README.html");
        stubRestClient.putResponse("/repos/spring-guides/understanding/contents/amqp/README.md", readmeHtml);

        MvcResult response = mockMvc.perform(get("/understanding/AMqp"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(html.select("title").text(), containsString("Understanding AMqp"));
        assertThat(html.select("h1").text(), containsString("Understanding: AMQP"));
    }

    @Test
    public void nonExistentGuideReturns404() throws Exception {
        mockMvc.perform(get("/understanding/non_existent"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

}
