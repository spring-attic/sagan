package integration.tools;

import integration.IntegrationTestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.stub;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO use fixture data for these tests
public class ToolsPagesTests extends IntegrationTestBase {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private RestTemplate restTemplate;

	@Before
	public void setup() throws IOException {
		InputStream response = new ClassPathResource("/sts_downloads.xml", getClass()).getInputStream();
		String responseXml = StreamUtils.copyToString(response, Charset.forName("UTF-8"));

		stub(restTemplate.getForObject(anyString(), eq(String.class))).toReturn(responseXml);

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void showsToolsIndex() throws Exception {
		this.mockMvc.perform(get("/tools"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"));
	}

	@Test
	public void showsStsIndex() throws Exception {
		this.mockMvc.perform(get("/tools/sts"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"));
	}

	@Test
	 public void showsAllStsDownloads() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(get("/tools/sts/all"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
		assertThat(document.select("h1").text(), equalTo("Spring Tool Suite™ Downloads"));
		assertThat(document.select(".platform h3").text(), containsString("Windows"));
		assertThat(document.select(".item--dropdown a").attr("href"), containsString("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-installer.exe"));
	}

	@Test
	public void showsGgtsIndex() throws Exception {
		this.mockMvc.perform(get("/tools/ggts"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"));
	}

	@Test
	public void showsAllGgtsDownloads() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(get("/tools/ggts/all"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
		assertThat(document.select("h1").text(), equalTo("Groovy/Grails Tool Suite™ Downloads"));
		assertThat(document.select(".platform h3").text(), containsString("Windows"));
		assertThat(document.select(".item--dropdown a").attr("href"), containsString("release/STS/3.3.0/dist/e4.3/groovy-grails-tool-suite-3.3.0.RELEASE-e4.3-win32-installer.exe"));
	}

	@Test
	public void showsEclipseIndex() throws Exception {
		InputStream response = new ClassPathResource("/eclipse.xml", getClass()).getInputStream();
		String responseXml = StreamUtils.copyToString(response, Charset.forName("UTF-8"));
		stub(restTemplate.getForObject(anyString(), eq(String.class))).toReturn(responseXml);

		MvcResult mvcResult = this.mockMvc.perform(get("/tools/eclipse"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());


		assertThat(document.select("h1").text(), equalTo("Eclipse"));
		assertThat(document.text(), containsString("Spring Tool Suite"));
		assertThat(document.text(), containsString("Groovy/Grails Tool Suite"));

		assertThat(document.text(), containsString("Windows"));
	}

}
