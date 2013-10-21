package examples.services;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import sagan.util.service.github.GitHubService;
import sagan.util.service.github.RepoContent;
import app.site.ApplicationConfiguration;
import org.jsoup.Jsoup;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import sagan.util.LongRunning;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public class GitHubServiceIntegrationExamples {

    @ClassRule
    public static LongRunning longRunning = LongRunning.create();

    @Autowired
    private GitHubService gitHubService;

    @Test
    public void testGuideRepoRequest() throws Exception {
        GitHubRepo[] guideRepos = this.gitHubService
                .getGitHubRepos("/orgs/spring-guides/repos?per_page=100");
        assertThat(guideRepos.length, greaterThan(0));

        Collection<GitHubRepo> tutorials = Collections2.filter(Arrays.asList(guideRepos),
                new Predicate<GitHubRepo>() {
                    @Override
                    public boolean apply(GitHubRepo input) {
                        return input.getName().startsWith("tut-");
                    }
                });

        assertThat(tutorials.size(), greaterThanOrEqualTo(2));
    }

    @Test
    public void getImage() {
        byte[] imageBytes = this.gitHubService.getGuideImage("gs-device-detection",
                "normal-browser.png");
        assertThat(imageBytes.length, greaterThan(0));
    }

    @Test
    public void rawFileAsHtml() {
        String html = this.gitHubService
                .getRawFileAsHtml("/repos/spring-io/spring.io/contents/README.md");
        assertThat(Jsoup.parse(html).select("h1").text(), equalTo("spring.io"));
    }

    @Test
    public void repoInfo() {
        GitHubRepo repoInfo = this.gitHubService.getRepoInfo("spring-io", "spring.io");
        assertThat(repoInfo.getName(), equalTo("spring.io"));
    }

    @Test
    public void renderToHtml() {
        String html = this.gitHubService.renderToHtml("Heading\n===");
        assertThat(Jsoup.parse(html).select("h1").text(), equalTo("Heading"));
    }

    @Test
    public void fetchRepoContents() throws Exception {
        List<RepoContent> repoContents = this.gitHubService
                .getRepoContents("fixtures/understanding");
        assertThat(repoContents.size(), greaterThan(8));
    }

    @Test
    public void fetchUserName() throws Exception {
        String userName = this.gitHubService.getNameForUser("cbeams");
        assertThat(userName, equalTo("Chris Beams"));
    }

}
