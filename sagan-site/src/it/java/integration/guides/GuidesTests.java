package integration.guides;

import sagan.util.FixtureLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import org.springframework.test.web.servlet.MvcResult;

import integration.IntegrationTestBase;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GuidesTests extends IntegrationTestBase {

    @Test
    public void showGuidesIndex() throws Exception {
        String repoList = FixtureLoader.load("/fixtures/github/githubRepoList.json");
        stubRestClient.putResponse("/orgs/spring-guides/repos", repoList);

        MvcResult response = this.mockMvc.perform(get("/guides"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(html.select("ul li.active").text(), equalTo("Guides"));
        Assert.assertThat(html.text(), containsString("Building a RESTful Web Service"));
        Assert.assertThat(html.text(), containsString("Learn how to create a RESTful web service with Spring"));
        Assert.assertThat(html.text(), containsString("Designing and Implementing RESTful Web Services with Spring"));
        Assert.assertThat(html.text(),
                containsString("Learn how to design and implement RESTful web services with Spring"));
    }

}
