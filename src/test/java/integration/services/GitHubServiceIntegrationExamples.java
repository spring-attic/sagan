package integration.services;

import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.domain.services.github.GitHubService;
import org.springframework.site.web.configuration.ApplicationConfiguration;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public class GitHubServiceIntegrationExamples {
	@Autowired
	private GitHubService gitHubService;

	@Test
	public void testGuideRepoRequest() throws Exception {
		GuideRepo[] guideRepos = gitHubService.getGuideRepos("/orgs/springframework-meta/repos?per_page=100");
		assertThat(guideRepos.length, greaterThan(0));
	}

	@Test
	public void getImage() {
		byte[] imageBytes = gitHubService.getGuideImage("gs-device-detection", "normal-browser.png");
		assertThat(imageBytes.length, greaterThan(0));
	}

	@Test
	public void rawFileAsHtml() {
		String html = gitHubService.getRawFileAsHtml("/repos/springframework-meta/springframework.org/contents/README.md");
		assertThat(Jsoup.parse(html).select("h1").text(), equalTo("springframework.org"));
	}

	@Test
	public void repoInfo() {
		GitHubRepo repoInfo = gitHubService.getRepoInfo("springframework-meta", "springframework.org");
		assertThat(repoInfo.getName(), equalTo("springframework.org"));
	}

	@Test
	public void renderToHtml() {
		String html = gitHubService.renderToHtml("Heading\n===");
		assertThat(Jsoup.parse(html).select("h1").text(), equalTo("Heading"));
	}

}
