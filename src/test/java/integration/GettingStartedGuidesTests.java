package integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration.configuration.SiteOfflineConfiguration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.guides.GettingStartedGuide;
import org.springframework.site.guides.GettingStartedService;
import org.springframework.site.guides.GuideRepo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SiteOfflineConfiguration.class, GettingStartedGuidesTests.OfflineConfiguration.class})
public class GettingStartedGuidesTests {

	public static final GettingStartedGuide GETTING_STARTED_GUIDE =
			new GettingStartedGuide("awesome-guide", "Awesome getting started guide that isn't helpful", "Related resources");

	@Configuration
	protected static class OfflineConfiguration {

		@Primary
		@Bean
		public GettingStartedService offlineGettingStartedService() {
			return new GettingStartedService() {
				@Override
				public GettingStartedGuide loadGuide(String guideId) {
					return GETTING_STARTED_GUIDE;
				}

				@Override
				public List<GuideRepo> listGuides() {
					ObjectMapper mapper = new ObjectMapper();
					try {
						String reposJson = "/org/springframework/site/guides/springframework-meta.repos.offline.json";
						InputStream json = new ClassPathResource(reposJson, getClass()).getInputStream();
						return mapper.readValue(json, new TypeReference<List<GuideRepo>>() {
						});
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public byte[] loadImage(String guideSlug, String imageName) {
					return new byte[0];
				}
			};
		}
	}

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void getGettingStartedGuidesListPage() throws Exception {
		this.mockMvc.perform(get("/guides/gs"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("rest-service")));
	}

	@Test
	public void getGettingStartedGuidesPage() throws Exception {
		MvcResult response = this.mockMvc.perform(get("/guides/gs/foo-bar/content"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.select("article").text(), is(GETTING_STARTED_GUIDE.getContent()));
		assertThat(html.select("aside#sidebar .related_resources").text(), is(GETTING_STARTED_GUIDE.getSidebar()));

		Element downloadLink = html.select("aside#sidebar a.github_download").first();
		assertThat(downloadLink, is(notNullValue()));
		assertThat(downloadLink.attr("href"), is(GETTING_STARTED_GUIDE.getZipUrl()));
	}

}
