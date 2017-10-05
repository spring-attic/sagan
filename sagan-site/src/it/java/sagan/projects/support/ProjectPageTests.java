package sagan.projects.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import saganx.AbstractIntegrationTests;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectPageTests extends AbstractIntegrationTests {

    private ResultActions result;
    private Document document;

    @Before
    public void setup() throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get("/project/spring-boot"));
        document = Jsoup.parse(result.andReturn().getResponse().getContentAsString());
    }

    @Test
    public void getStatusOk() throws Exception {
        result.andExpect(status().isOk());
    }

    @Test
    public void getContentType() throws Exception {
        result.andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void getSidebar() throws Exception {
        Document document = Jsoup.parse(result.andReturn().getResponse().getContentAsString());
        List<String> titles = document.select(".sidebar .sidebar_project a").stream()
                .map(Element::text).collect(toList());
        assertThat(titles, hasItems("Spring Data", "Spring Framework", "Spring Boot"));
        assertThat(titles, not(hasItems("Spring XD")));
    }

    @Test
    public void sidebarHighlightCurrentProject() throws Exception {
        List<String> titles = document.select(".sidebar .sidebar_project.active").stream()
                .map(Element::text).collect(toList());
        assertThat(titles.size(), is(1));
        assertThat(titles.get(0), is("Spring Boot"));
    }

    @Test
    public void getQuickStart() throws Exception {
        assertThat(document.select(".quickstart").size(), is(1));

        Elements quickStartTitle = document.select(".quickstart--title");
        assertThat(quickStartTitle.size(), is(1));
        assertThat(quickStartTitle.first().text(), is("Quick start"));

        Elements quickStartSubtitle = document.select(".quickstart--subtitle");
        assertThat(quickStartSubtitle.size(), is(1));
        assertThat(quickStartSubtitle.first().text(), is("Bootstrap your application with Spring Initializr"));

        Elements quickStartButton = document.select(".quickstart--button");
        assertThat(quickStartButton.size(), is(1));
        assertThat(quickStartButton.first().text(), is("Get started"));
        assertThat(quickStartButton.first().attr("href"), startsWith("https://start.spring.io/"));
    }

    @Test
    public void getHeader() throws Exception {
        assertThat(document.select(".project--header").size(), is(1));

        Elements githubLink = document.select(".project--header .link--github");
        assertThat(githubLink.size(), is(1));
        assertThat(githubLink.first().attr("href"), is("https://github.com/spring-projects/spring-boot"));

        Elements stackoverflowLink = document.select(".project--header .link--stackoverflow");
        assertThat(stackoverflowLink.size(), is(1));
        assertThat(stackoverflowLink.first().attr("href"), is("https://stackoverflow.com/questions/tagged/spring-boot"));

        assertThat(document.select(".project--header .version").first().text(), containsString("v1.5.7"));
    }

    @Test
    public void getSpringBootConfig() {
        Elements springBootConfig = document.select(".spring-boot-config");
        assertThat(springBootConfig, hasSize(1));

        Elements springBootConfigTitle = springBootConfig.select(".spring-boot-config--subtitle");
        assertThat(springBootConfigTitle.text(), is("Spring Boot Config"));

        String cleanHtml = springBootConfig.html().replaceAll("\\s+", " ");
        assertThat(cleanHtml, containsString("Lorem ipsum dolor sit amet, <a>consectetur</a> adipiscing elit"));
    }

    @Test
    public void getSpringBootConfigIsHiddenWhenItDoesNotExist() throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get("/project/spring-data-redis"));
        document = Jsoup.parse(result.andReturn().getResponse().getContentAsString());

        assertThat(document.select(".spring-boot-config"), hasSize(0));
    }
}
