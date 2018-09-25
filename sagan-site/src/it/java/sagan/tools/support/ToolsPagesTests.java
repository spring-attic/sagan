package sagan.tools.support;

import sagan.support.Fixtures;
import saganx.AbstractIntegrationTests;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.stub;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ToolsPagesTests extends AbstractIntegrationTests {

    @Autowired
    private RestTemplate restTemplate;

    @Before
    public void setup() throws IOException {
        String responseXml = Fixtures.load("/fixtures/tools/sts_downloads.xml");

        stub(restTemplate.getForObject(anyString(), eq(String.class))).toReturn(responseXml);
    }

    @Test
    public void showsToolsIndex() throws Exception {
        mockMvc.perform(get("/tools"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void showsStsIndex() throws Exception {
        mockMvc.perform(get("/tools3/sts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void showsAllStsGaDownloads() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/tools3/sts/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertThat(document.select("h1").text(), equalTo("Spring Tool Suite™ 3 Downloads"));
        assertThat(document.select(".ga--release h2.tool-versions--version").text(), allOf(containsString("STS"),
                containsString("RELEASE")));
        assertThat(document.select(".platform h3").text(), containsString("Windows"));

        assertThat(document.select(".ga--release .item--dropdown a").attr("href"), allOf(
                containsString("http://download.springsource.com/release/STS/"),
                containsString("spring-tool-suite"),
                containsString("win32.zip")));
    }

    @Test
    public void showsAllStsMilestoneDownloads() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/tools3/sts/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertThat(document.select(".milestone--release h2.tool-versions--version").text(), allOf(
                containsString("STS"),
                containsString(".M")));

        assertThat(
                document.select(".milestone--release .item--dropdown a").attr("href"), allOf(
                        containsString("http://download.springsource.com/milestone/STS/"),
                        containsString("spring-tool-suite"),
                        containsString("win32.zip")));
    }

    @Test
    public void hidesStsMilestoneDownloadsIfNotAvailable() throws Exception {
        String responseXml = Fixtures.load("/fixtures/tools/sts_downloads_without_milestones.xml");
        stub(restTemplate.getForObject(anyString(), eq(String.class))).toReturn(responseXml);

        MvcResult mvcResult = mockMvc.perform(get("/tools3/sts/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertThat(document.select(".milestone--release h2.tool-versions--version").text(),
                not(allOf(containsString("STS"), containsString(".M"))));

        assertThat(
                document.select(".milestone--release .item--dropdown a").attr("href"),
                not(allOf(
                        containsString("http://download.springsource.com/milestone/STS/"),
                        containsString("spring-tool-suite"),
                        containsString("win32.zip"))));

    }

    @Test
    public void showsLegacyStsGaDownloads() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/tools3/sts/legacy"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertThat(document.select("h1").text(), equalTo("Previous Spring Tool Suite™ 3 Downloads"));
        assertThat(document.select(".ga--release h2.tool-versions--version").text(), allOf(containsString("STS"),
                containsString("RELEASE")));
        assertThat(document.select(".platform h3").text(), containsString("Windows"));

        assertThat(document.select(".ga--release .item--dropdown a").first().attr("href"), allOf(
                containsString("http://download.springsource.com/release/STS/"),
                containsString("spring-tool-suite"),
                containsString("win32-installer.exe")));

        assertThat(document.select(".ga--release").size(), equalTo(24));
    }

    @Test
    public void showsEclipseIndex() throws Exception {
        String responseXml = Fixtures.load("/fixtures/tools/eclipse.xml");
        stub(restTemplate.getForObject(anyString(), eq(String.class))).toReturn(responseXml);

        MvcResult mvcResult = mockMvc.perform(get("/tools3/eclipse"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertThat(document.select("h1").text(), containsString("Eclipse"));
        assertThat(document.text(), containsString("Spring Tool Suite"));
        assertThat(document.text(), containsString("Windows"));
    }

    @Test
    public void showsStsWelcome() throws Exception {
        mockMvc.perform(get("/tools3/sts/welcome?version=3.2.0.RELEASE&os=macosx.cocoa.x86_64"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));

    }

}
