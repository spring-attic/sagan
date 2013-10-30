package integration.understanding;

import sagan.util.FixtureLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import org.springframework.test.web.servlet.MvcResult;

import integration.AbstractIntegrationTests;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UnderstandingGuidesTests extends AbstractIntegrationTests {

    @Test
    public void getExistingGuide() throws Exception {
        String readmeHtml = FixtureLoader.load("/fixtures/understanding/amqp/README.html");
        stubRestClient.putResponse("/repos/spring-guides/understanding/contents/amqp/README.md", readmeHtml);

        String sidebarHtml = FixtureLoader.load("/fixtures/understanding/amqp/SIDEBAR.html");
        stubRestClient.putResponse("/repos/spring-guides/understanding/contents/amqp/SIDEBAR.md", sidebarHtml);

        MvcResult response = this.mockMvc.perform(get("/understanding/AMqp"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(html.select("title").text(), containsString("Understanding AMqp"));
        assertThat(html.select("h1").text(), containsString("Understanding: AMQP"));
        assertThat(html.select("aside").text(), containsString("Messaging with RabbitMQ"));
    }

    @Test
    public void nonExistentGuideReturns404() throws Exception {
        this.mockMvc.perform(get("/understanding/non_existent"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

}
