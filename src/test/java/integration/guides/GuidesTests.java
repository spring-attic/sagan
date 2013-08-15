package integration.guides;

import integration.IntegrationTestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.site.test.FixtureLoader;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GuidesTests extends IntegrationTestBase {

	@Test
	public void showsGuidesIndex() throws Exception {
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


		MvcResult result = this.mockMvc.perform(get("/guides"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document document = Jsoup.parse(result.getResponse().getContentAsString());
		assertThat(document.select("ul li.active").text(), equalTo("Guides"));
	}
}
