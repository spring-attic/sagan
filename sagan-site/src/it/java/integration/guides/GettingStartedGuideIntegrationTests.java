package integration.guides;

import sagan.util.Fixtures;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import org.springframework.test.web.servlet.MvcResult;

import integration.AbstractIntegrationTests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GettingStartedGuideIntegrationTests extends AbstractIntegrationTests {

    @Test
    public void showGuide() throws Exception {
        String gsRestServiceRepo = Fixtures.load("/fixtures/github/gs-rest-service-repo.json");
        String gsRestServiceReadme = Fixtures.load("/fixtures/github/gs-rest-service-readme.json");
        stubRestClient.putResponse("/repos/spring-guides/gs-rest-service", gsRestServiceRepo);

        stubRestClient.putResponse("/repos/spring-guides/gs-rest-service/readme", gsRestServiceReadme);
        stubRestClient.putResponse("/repos/spring-guides/gs-rest-service/contents/README.md",
                "guide body");
        stubRestClient.putResponse("/repos/spring-guides/gs-rest-service/contents/SIDEBAR.md",
                "sidebar content");

        MvcResult response = this.mockMvc.perform(get("/guides/gs/rest-service/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(html.select(".article-body").text(), is("guide body"));
        assertThat(html.select("aside#sidebar .related_resources").text(), is("sidebar content"));

        assertThat(html.select(".title").text(), equalTo("Building a RESTful Web Service"));

        Element downloadLink = html.select("aside#sidebar a.github_download").first();
        assertThat(downloadLink, is(notNullValue()));
        assertThat(downloadLink.attr("href"), is("https://github.com/spring-guides/gs-rest-service/archive/master.zip"));
    }

}
