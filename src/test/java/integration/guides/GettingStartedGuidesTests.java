package integration.guides;

import integration.IntegrationTestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.site.test.FixtureLoader;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GettingStartedGuidesTests extends IntegrationTestBase {

	@Test
	public void getGettingStartedGuidesListPage() throws Exception {
		String repoList = FixtureLoader.load("/fixtures/github/githubRepoList.json");
		stubRestClient.putResponse("/orgs/springframework-meta/repos", repoList);

		String gsRestServiceRepo = FixtureLoader.load("/fixtures/github/gs-rest-service-repo.json");
		stubRestClient.putResponse("/repos/springframework-meta/gs-rest-service", gsRestServiceRepo);

		stubRestClient.putResponse("/repos/springframework-meta/gs-rest-service/contents/README.md",
				"guide body");

		stubRestClient.putResponse("/repos/springframework-meta/gs-rest-service/contents/SIDEBAR.md",
				"sidebar content");

		stubRestClient.putResponse("/repos/springframework-meta/tut-rest/contents/README.md",
				"tutorial body");


		MvcResult response = this.mockMvc.perform(get("/guides"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.text(), containsString("Building a RESTful Web Service"));
		assertThat(html.text(), containsString("Learn how to create a RESTful web service with Spring"));
	}

	@Test
	public void getGettingStartedGuidesPage() throws Exception {
		String gsRestServiceRepo = FixtureLoader.load("/fixtures/github/gs-rest-service-repo.json");
		stubRestClient.putResponse("/repos/springframework-meta/gs-rest-service", gsRestServiceRepo);

		stubRestClient.putResponse("/repos/springframework-meta/gs-rest-service/contents/README.md",
				"guide body");
		stubRestClient.putResponse("/repos/springframework-meta/gs-rest-service/contents/SIDEBAR.md",
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
		assertThat(downloadLink.attr("href"), is("https://github.com/springframework-meta/gs-rest-service/archive/master.zip"));
	}

}
