package sagan.projects.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sagan.support.Fixtures;
import saganx.AbstractIntegrationTests;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectPagesTests extends AbstractIntegrationTests {

    @Before
    public void setup() throws Exception {
        stubRestClient.putResponse("/orgs/spring-guides/repos",
                Fixtures.load("/fixtures/github/githubRepoListWithSpringBoot.json"));
    }

    @Test
    public void getStatusOk() throws Exception {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/projects/spring-boot"));
        result.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void getSidebar() throws Exception {
		Document document = documentForUrlPath("/projects/spring-boot");
        List<String> titles = document.select(".sidebar .sidebar_project a").stream()
                .map(Element::text).collect(toList());
        assertThat(titles, hasItems("Spring Data", "Spring Framework", "Spring Boot")); // active
        assertThat(titles, not(hasItems("Spring XD"))); // not active
        assertThat(titles, not(hasItems("spring-data-solr"))); // children of "umbrella" projects
    }

    @Test
    public void sidebar_highlightsUmbrellaProjectAndShowsChildren() throws Exception {
        Document document = documentForUrlPath("/projects/spring-data");

        Elements activeProject = document.select(".sidebar .sidebar_project.active");
        assertThat(activeProject, hasSize(1));
		assertThat(activeProject.select("div > a").text(), is("Spring Data"));

		Elements childProjects = activeProject.select(".sidebar_child");
        assertThat(childProjects, hasSize(15));

        List<String> childTitles = childProjects.stream()
                .map(Element::text)
                .map(String::toLowerCase)
                .collect(toList());
        assertThat(childTitles, hasItems("spring data rest"));

        List<String> childHrefs = childProjects.stream()
                .map(element -> element.select("a").attr("href"))
                .collect(toList());
        assertThat(childHrefs, hasItems(containsString("/spring-data-rest")));
    }

    @Test
    public void sidebar_hidesChildrenOfNotSelectedUmbrellaProjects() throws Exception {
        Document document = documentForUrlPath("/projects/spring-vault");

        Elements children = document.select(".sidebar_child");
        assertThat(children, hasSize(0));
    }

    @Test
    public void sidebar_selectingChildHighlightsItselfAndShowsSiblings() throws Exception {
        Document document = documentForUrlPath("/projects/spring-data-rest");

        Elements activeProject = document.select(".sidebar_child.active");
        List<String> titles = activeProject.stream()
                .map(element -> element.select("a").text())
                .map(String::toLowerCase)
                .collect(toList());
        assertThat(titles, hasSize(1));
        assertThat(titles, hasItems("spring data rest"));
    }

    @Test
    public void getQuickStart() throws Exception {
		Document document = documentForUrlPath("/projects/spring-boot");
        assertThat(document.select(".quickstart").size(), is(1));
        
        Elements quickStartTitle = document.select(".quickstart h2");
        assertThat(quickStartTitle.size(), is(1));
        assertThat(quickStartTitle.first().text(), is("Quick start"));

        Elements quickStartContent = document.select(".quickstart div");
        assertThat(quickStartContent.size(), is(1));
        assertThat(quickStartContent.first().text(), is("Bootstrap your application with Spring Initializr."));

        Elements quickStartLink = quickStartContent.select("a");
        assertThat(quickStartLink.size(), is(1));
        assertThat(quickStartLink.first().text(), is("Spring Initializr"));
        assertThat(quickStartLink.first().attr("href"), startsWith("https://start.spring.io/"));
    }

    @Test
    public void getHeader() throws Exception {
		Document document = documentForUrlPath("/projects/spring-boot");
        assertThat(document.select(".project--header").size(), is(1));

        Elements githubLink = document.select(".project--header .link--github");
        assertThat(githubLink.size(), is(1));
        assertThat(githubLink.first().attr("href"), is("https://github.com/spring-projects/spring-boot"));

        Elements stackoverflowLink = document.select(".project--header .link--stackoverflow");
        assertThat(stackoverflowLink.size(), is(1));
        assertThat(stackoverflowLink.first().attr("href"),
                is("https://stackoverflow.com/questions/tagged/spring-boot"));

        assertThat(document.select(".project--header .version").first().text(), containsString("1.5.7"));
    }

    @Test
    public void getSpringBootConfig() throws Exception {
		Document document = documentForUrlPath("/projects/spring-boot");
        Elements springBootConfig = document.select(".spring-boot-config");
        assertThat(springBootConfig, hasSize(1));

        Elements springBootConfigTitle = springBootConfig.select("h2");
        assertThat(springBootConfigTitle.text(), is("Spring Boot Config"));

        String cleanHtml = springBootConfig.html().replaceAll("\\s+", " ");
        assertThat(cleanHtml, containsString("Lorem ipsum dolor sit amet, <a>consectetur</a> adipiscing elit"));
    }

    @Test
    public void getSpringBootConfigIsHiddenWhenItDoesNotExist() throws Exception {
        Document document = documentForUrlPath("/projects/spring-data-redis");

        assertThat(document.select(".spring-boot-config"), hasSize(0));
    }

    @Test
    public void getHeaderOmitsVersionWhenThereIsNoCurrentRelease() throws Exception {
        Document document = documentForUrlPath("/projects/spring-data-redis");

        assertThat(document.select(".project--header .version"), hasSize(0));
    }

    @Test
    public void getFeatures() throws Exception {
		Document document = documentForUrlPath("/projects/spring-boot");
        Elements features = document.select(".project-overview");
        assertThat(features, hasSize(1));

        String featuresHtml = features.html()
                .replaceAll("\\n\\s+", "");
        assertThat(featuresHtml, containsString("<ul><li>hello world</li></ul>"));
    }

    @Test
    public void getSubheaderHasBootstrapTabsMarkup() throws Exception {
		Document document = documentForUrlPath("/projects/spring-boot");
        Elements subheader = document.select(".nav.nav-tabs");
        assertThat(subheader, hasSize(1));

        Elements tabs = subheader.select("a[data-toggle=tab]");
        assertThat(tabs, hasSize(3));
        assertThat(tabs.get(0).text(), is("Overview"));
        assertThat(tabs.get(1).text(), is("Learn"));
        assertThat(tabs.get(2).text(), is("Samples"));
        assertThat(tabs.get(0).attr("href"), is("#overview"));
        assertThat(tabs.get(1).attr("href"), is("#learn"));
        assertThat(tabs.get(2).attr("href"), is("#samples"));

        Elements tabsContent = document.select(".tab-content .tab-pane");
        assertThat(tabsContent, hasSize(3));
        assertThat(tabsContent.get(0).id(), is("overview"));
        assertThat(tabsContent.get(1).id(), is("learn"));
        assertThat(tabsContent.get(2).id(), is("samples"));
    }

    @Test
    public void getGuides() throws Exception {
		Document document = documentForUrlPath("/projects/spring-boot");
        Elements learnSection = document.select("#learn");
        assertThat(learnSection, hasSize(1));

        Element learnTitle = learnSection.select(".project--guides--title").first();
        assertThat(learnTitle.text(), is("Guides"));

        Elements guides = learnSection.select(".project--guides ul li");
        assertThat(guides, hasSize(1));

        Element firstGuide = guides.get(0);
        Elements title = firstGuide.select(".project--sample--title");
        Elements subtitle = firstGuide.select(".project--sample--description");
        Element guideLink = firstGuide.select("a").first();

        assertThat(title.text(), is("Building a RESTful Web Service"));
        assertThat(subtitle.text(), is("Learn how to create a RESTful web service with Spring."));
        assertThat(guideLink.attr("href"), is("/guides/gs/spring-boot"));
    }

    @Test
    public void getDocumentation() throws Exception {
		Document document = documentForUrlPath("/projects/spring-boot");
        Element documentationSection = document.select(".project--documentation").first();

        Element documentationTitle = documentationSection.select("h2").first();
        assertThat(documentationTitle.text(), is("Documentation"));

        Elements docs = document.select("tr");
        assertThat(docs, hasSize(5));

        List<String> displayNames = docs.stream()
                .map(element -> element.select(".release-display-name").text())
                .collect(toList());

        assertThat(displayNames, contains("1.5.7", "2.0.0 M4", "2.0.0", "1.5.8", "1.4.7"));

        List<String> statuses = docs.select(".label").stream()
                .map(element -> element.text())
                .collect(toList());

        assertThat(statuses, contains("CURRENT", "GA", "PRE", "SNAPSHOT", "SNAPSHOT", "GA"));

        List<String> links = docs.select("a").stream()
                .map(element -> element.attr("href"))
                .collect(toList());

        assertThat(links, contains(
				"http://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/reference/htmlsingle/",
				"http://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/api/",
				"http://docs.spring.io/spring-boot/docs/2.0.0.M4/reference/htmlsingle/",
                "http://docs.spring.io/spring-boot/docs/2.0.0.M4/api/",
                "http://docs.spring.io/spring-boot/docs/2.0.0.BUILD-SNAPSHOT/reference/htmlsingle/",
                "http://docs.spring.io/spring-boot/docs/2.0.0.BUILD-SNAPSHOT/api/",
                "http://docs.spring.io/spring-boot/docs/1.5.8.BUILD-SNAPSHOT/reference/htmlsingle/",
				"http://docs.spring.io/spring-boot/docs/1.5.8.BUILD-SNAPSHOT/api/",
                "http://docs.spring.io/spring-boot/docs/1.4.7.RELEASE/reference/htmlsingle/",
				"http://docs.spring.io/spring-boot/docs/1.4.7.RELEASE/api/"
        ));
    }

    @Test
    public void referenceDocShowsAllReleasesWhenThereIsNoCurrentRelease() throws Exception {
        Document document = documentForUrlPath("/projects/platform");
        Element documentationSection = document.select(".project--documentation").first();
        Elements versions = documentationSection.select(".table--documentation tr");
        assertThat(versions, hasSize(3));
        assertThat(documentationSection.select(".label.current"), hasSize(0));
    }

    @Test
    public void referenceDocIsHiddenWhenThereAreNoReleases() throws Exception {
        Document document = documentForUrlPath("/projects/spring-xd");

        assertThat(document.select(".project-reference"), hasSize(0));
    }

    @Test
    public void apiDocShowsAllReleasesWhenThereIsNoCurrentRelease() throws Exception {
        Document document = documentForUrlPath("/projects/platform");

        Element documentationSection = document.select(".project--documentation").first();
        Elements versions = documentationSection.select(".table--documentation tr");
        assertThat(versions, hasSize(3));
        assertThat(document.select(".project--header .version"), hasSize(0));
    }

    @Test
    public void apiDocIsHiddenWhenThereAreNoReleases() throws Exception {
        Document document = documentForUrlPath("/projects/spring-xd");

        assertThat(document.select(".project-api"), hasSize(0));
    }

    @Test
    public void getSamples() throws Exception {
		Document document = documentForUrlPath("/projects/spring-boot");
        Elements samplesSection = document.select("#samples");
        assertThat(samplesSection, hasSize(1));

        Element samplesTitle = samplesSection.select("h2").first();
        assertThat(samplesTitle.text(), is("A few examples to try out:"));


        Elements samples = samplesSection.select(".project--samples li");
        assertThat(samples, hasSize(6));

        Element firstSample = samples.get(0);
        Elements title = firstSample.select(".project--sample--title");
        Elements description = firstSample.select(".project--sample--description");
        Element sample = firstSample.select("a").first();

        assertThat(title.text(), is("Simple"));
        assertThat(description.text(), is("Simple command line application"));
        assertThat(sample.attr("href"), is("https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-simple"));
    }

    @Test
    public void onlyOverviewSectionWhenEmpty() throws Exception {
        Document document = documentForUrlPath("/projects/spring-xd");

        Elements tabs = document.select(".nav.nav-tabs a[data-toggle=tab]");
        assertThat(tabs, hasSize(1));

        assertThat(document.select("#overview"), hasSize(1));
        assertThat(document.select("#learn"), hasSize(0));
        assertThat(document.select("#samples"), hasSize(0));
    }

    private Document documentForUrlPath(String urlPath) throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(urlPath));
        return Jsoup.parse(result.andReturn().getResponse().getContentAsString());
    }
}
