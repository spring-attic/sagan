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

public class ProjectPageTests extends AbstractIntegrationTests {

    private ResultActions result;
    private Document document;

    @Before
    public void setup() throws Exception {
        stubRestClient.putResponse("/orgs/spring-guides/repos",
                Fixtures.load("/fixtures/github/githubRepoListWithSpringBoot.json"));
        stubRestClient.putResponseBytes("/repos/spring-guides/gs-spring-boot/zipball",
                Fixtures.loadData("../../gs-spring-boot.zip"));

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
        List<String> titles = document.select(".sidebar .sidebar_project a").stream()
                .map(Element::text).collect(toList());
        assertThat(titles, hasItems("Spring Data", "Spring Framework", "Spring Boot")); // active
        assertThat(titles, not(hasItems("Spring XD"))); // not active
        assertThat(titles, not(hasItems("spring-data-solr"))); // children of "umbrella" projects
    }

    @Test
    public void sidebar_highlightsUmbrellaProjectAndShowsChildren() throws Exception {
        Document document = documentForUrlPath("/project/spring-data");

        Elements activeProject = document.select(".sidebar .sidebar_project.active");

        List<String> titles = activeProject.stream()
                .map(element -> element.select("> div > a").text())
                .collect(toList());
        assertThat(titles, hasSize(1));
        assertThat(titles.get(0), is("Spring Data"));

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
        Document document = documentForUrlPath("/project/spring-vault");

        Elements children = document.select(".sidebar_child");
        assertThat(children, hasSize(0));
    }

    @Test
    public void sidebar_selectingChildHighlightsItselfAndShowsSiblings() throws Exception {
        Document document = documentForUrlPath("/project/spring-data-rest");

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
        assertThat(stackoverflowLink.first().attr("href"),
                is("https://stackoverflow.com/questions/tagged/spring-boot"));

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
        Document document = documentForUrlPath("/project/spring-data-redis");

        assertThat(document.select(".spring-boot-config"), hasSize(0));
    }

    @Test
    public void getHeaderOmitsVersionWhenThereIsNoCurrentRelease() throws Exception {
        Document document = documentForUrlPath("/project/spring-data-redis");

        assertThat(document.select(".project--header .version"), hasSize(0));
    }

    @Test
    public void getFeatures() throws Exception {
        Elements features = document.select(".project-overview");
        assertThat(features, hasSize(1));

        String featuresHtml = features.html()
                .replaceAll("\\n\\s+", "");
        assertThat(featuresHtml, containsString("<ul><li>hello world</li></ul>"));
    }

    @Test
    public void getSubheaderHasBootstrapTabsMarkup() throws Exception {
        Elements subheader = document.select(".nav.nav-tabs");
        assertThat(subheader, hasSize(1));

        Elements tabs = subheader.select("a[data-toggle=tab]");
        assertThat(tabs, hasSize(2));
        assertThat(tabs.get(0).text(), is("Overview"));
        assertThat(tabs.get(1).text(), is("Learn"));
        assertThat(tabs.get(0).attr("href"), is("#overview"));
        assertThat(tabs.get(1).attr("href"), is("#learn"));

        Elements tabsContent = document.select(".tab-content .tab-pane");
        assertThat(tabsContent, hasSize(2));
        assertThat(tabsContent.get(0).id(), is("overview"));
        assertThat(tabsContent.get(1).id(), is("learn"));
    }

    @Test
    public void getGuides() throws Exception {
        Elements learnSection = document.select("#learn");
        assertThat(learnSection, hasSize(1));

        Element learnTitle = learnSection.select(".project--guides--title").first();
        assertThat(learnTitle.text(), is("Guides"));

        Elements guides = learnSection.select(".project--guide");
        assertThat(guides, hasSize(1));

        Element firstGuide = guides.get(0);
        Elements title = firstGuide.select(".project--guide--title");
        Elements subtitle = firstGuide.select(".project--guide--subtitle");
        Element guideLink = firstGuide.select("a").first();

        assertThat(title.text(), is("Building a RESTful Web Service"));
        assertThat(subtitle.text(), is("Learn how to create a RESTful web service with Spring."));
        assertThat(guideLink.attr("href"), is("/guides/gs/spring-boot"));
    }

    @Test
    public void getReferenceDocumentation() {
        Element documentationSection = document.select(".project--documentation").first();

        Element documentationTitle = documentationSection.select("h2").first();
        assertThat(documentationTitle.text(), is("Documentation"));

        Element referenceDoc = documentationSection.select(".project-reference").first();
        Element referenceSubtitle = referenceDoc.select("h3").first();
        assertThat(referenceSubtitle.text(), is("Reference doc."));

        Element currentVersion = referenceDoc.select(".project-reference--current-version").first();
        assertThat(currentVersion.select(".release-display-name").text(), is("1.5.7"));
        assertThat(currentVersion.select(".release-status").text(), is("GA CURRENT"));
        assertThat(currentVersion.select("a").attr("href"),
                is("http://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/reference/htmlsingle/"));

        Elements versions = referenceDoc.select(".project-reference--version");
        assertThat(versions, hasSize(4));

        List<String> displayNames = versions.stream()
                .map(element -> element.select(".release-display-name").text())
                .collect(toList());

        assertThat(displayNames, contains("2.0.0 M4", "2.0.0", "1.5.8", "1.4.7"));

        List<String> statuses = versions.stream()
                .map(element -> element.select(".release-status").text())
                .collect(toList());

        assertThat(statuses, contains("PRE", "SNAPSHOT", "SNAPSHOT", "GA"));

        List<String> links = versions.stream()
                .map(element -> element.select("a").attr("href"))
                .collect(toList());

        assertThat(links, contains(
                "http://docs.spring.io/spring-boot/docs/2.0.0.M4/reference/htmlsingle/",
                "http://docs.spring.io/spring-boot/docs/2.0.0.BUILD-SNAPSHOT/reference/htmlsingle/",
                "http://docs.spring.io/spring-boot/docs/1.5.8.BUILD-SNAPSHOT/reference/htmlsingle/",
                "http://docs.spring.io/spring-boot/docs/1.4.7.RELEASE/reference/htmlsingle/"
        ));

    }

    @Test
    public void referenceDocShowsAllReleasesWhenThereIsNoCurrentRelease() throws Exception {
        Document document = documentForUrlPath("/project/platform");

        Element documentationSection = document.select(".project--documentation").first();
        Element referenceDoc = documentationSection.select(".project-reference").first();

        Elements versions = referenceDoc.select(".project-reference--version");
        assertThat(versions, hasSize(3));

        assertThat(document.select(".project-reference--releases").hasClass("in"), is(true));
        assertThat(document.select(".project-reference--version-button"), hasSize(0));
    }

    @Test
    public void referenceDocIsHiddenWhenThereAreNoReleases() throws Exception {
        Document document = documentForUrlPath("/project/spring-xd");

        assertThat(document.select(".project-reference"), hasSize(0));
    }

    @Test
    public void getAPIDocumentation() {
        Element documentationSection = document.select(".project--documentation").first();

        Element apiDoc = documentationSection.select(".project-api").first();
        Element apiSubtitle = apiDoc.select("h3").first();
        assertThat(apiSubtitle.text(), is("API doc."));

        Element currentVersion = apiDoc.select(".project-api--current-version").first();
        assertThat(currentVersion.select(".release-display-name").text(), is("1.5.7"));
        assertThat(currentVersion.select(".release-status").text(), is("GA CURRENT"));
        assertThat(currentVersion.select("a").attr("href"),
                is("http://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/api/"));

        Elements versions = apiDoc.select(".project-api--version");
        assertThat(versions, hasSize(4));

        List<String> displayNames = versions.stream()
                .map(element -> element.select(".release-display-name").text())
                .collect(toList());

        assertThat(displayNames, contains("2.0.0 M4", "2.0.0", "1.5.8", "1.4.7"));

        List<String> statuses = versions.stream()
                .map(element -> element.select(".release-status").text())
                .collect(toList());

        assertThat(statuses, contains("PRE", "SNAPSHOT", "SNAPSHOT", "GA"));

        List<String> links = versions.stream()
                .map(element -> element.select("a").attr("href"))
                .collect(toList());

        assertThat(links, contains(
                "http://docs.spring.io/spring-boot/docs/2.0.0.M4/api/",
                "http://docs.spring.io/spring-boot/docs/2.0.0.BUILD-SNAPSHOT/api/",
                "http://docs.spring.io/spring-boot/docs/1.5.8.BUILD-SNAPSHOT/api/",
                "http://docs.spring.io/spring-boot/docs/1.4.7.RELEASE/api/"
        ));
    }

    @Test
    public void apiDocShowsAllReleasesWhenThereIsNoCurrentRelease() throws Exception {
        Document document = documentForUrlPath("/project/platform");

        Element documentationSection = document.select(".project--documentation").first();
        Element apiDoc = documentationSection.select(".project-api").first();

        Elements versions = apiDoc.select(".project-api--version");
        assertThat(versions, hasSize(3));

        assertThat(document.select(".project-api--releases").hasClass("in"), is(true));
        assertThat(document.select(".project-api--version-button"), hasSize(0));
    }

    @Test
    public void apiDocIsHiddenWhenThereAreNoReleases() throws Exception {
        Document document = documentForUrlPath("/project/spring-xd");

        assertThat(document.select(".project-api"), hasSize(0));
    }

    private Document documentForUrlPath(String urlPath) throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(urlPath));
        return Jsoup.parse(result.andReturn().getResponse().getContentAsString());
    }
}
