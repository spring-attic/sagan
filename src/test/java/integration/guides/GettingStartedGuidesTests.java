package integration.guides;

import integration.IntegrationTestBase;
import integration.configuration.IntegrationTestsConfiguration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GettingStartedGuidesTests extends IntegrationTestBase {
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void getGettingStartedGuidesListPage() throws Exception {
		MvcResult response = this.mockMvc.perform(get("/guides/gs"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		Element restServiceGuide = html.select(".guide").first();
		assertThat(restServiceGuide.select(".title").text(), equalTo("Building a RESTful Web Service"));
		assertThat(restServiceGuide.select(".subtitle").text(), equalTo("Learn how to create a REST web service with Spring"));
		assertThat(restServiceGuide.select("a").attr("href"), equalTo("/guides/gs/rest-service/content"));
	}

	@Test
	public void getGettingStartedGuidesPage() throws Exception {
		MvcResult response = this.mockMvc.perform(get("/guides/gs/foo-bar/content"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.select(".article-body").text(), is(IntegrationTestsConfiguration.GETTING_STARTED_GUIDE.getContent()));
		assertThat(html.select("aside#sidebar .related_resources").text(), is(IntegrationTestsConfiguration.GETTING_STARTED_GUIDE.getSidebar()));

		assertThat(html.select(".title").text(), equalTo("Awesome Guide"));
		assertThat(html.select(".subtitle").text(), is("Awesome getting started guide that isn't helpful"));

		Element downloadLink = html.select("aside#sidebar a.github_download").first();
		assertThat(downloadLink, is(notNullValue()));
		assertThat(downloadLink.attr("href"), is(IntegrationTestsConfiguration.GETTING_STARTED_GUIDE.getZipUrl()));
	}

}
