package sagan.guides.support;

import sagan.support.github.GitHubClient;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.StreamUtils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class DynamicSidebarTests {

    private static final String README_REST_ZIPBALL = "/repos/spring-guides/gs-rest-service/zipball";

    @Mock
    GitHubClient github;

    GuideOrganization org;

    @Before
    public void setup() throws IOException {
        initMocks(this);
        org = new GuideOrganization("spring-guides", "orgs", github);
    }

    @Test
    public void loadGuideContentWithCategories() throws IOException {
        InputStream inputStream = new DefaultResourceLoader().getResource("classpath:/gs-rest-service-with-tags.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();

        given(github.sendRequestForDownload(README_REST_ZIPBALL)).willReturn(zipContents);

        AsciidocGuide guide = org.getAsciidocGuide(README_REST_ZIPBALL);

        assertThat(guide.getTags(), hasItems("rest", "json", "springmvc"));
        assertThat(guide.getTags(), not(hasItems("reactor", "data")));
        assertThat(guide.getProjects(), empty());
        assertThat(guide.getTableOfContents(), startsWith("<ul class=\"sectlevel1\">"));
        assertThat(guide.getTableOfContents(), not(containsString("<ul class=\"sectlevel2\">")));
        assertThat(guide.getUnderstandingDocs().keySet(), hasItems("/understanding/JSON", "/understanding/WAR",
                "/understanding/view-templates", "/understanding/application-context", "/understanding/REST",
                "/understanding/Tomcat"));
        assertThat(guide.getUnderstandingDocs().values(), hasItems("JSON", "WAR", "View Technology",
                "Spring Application Context", "RESTful Web Service", "Tomcat"));
    }

    @Test
    public void loadGuideContentWithoutCategories() throws IOException {
        InputStream inputStream = new DefaultResourceLoader().getResource("classpath:/gs-rest-service-without-tags.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();

        given(github.sendRequestForDownload(README_REST_ZIPBALL)).willReturn(zipContents);

        AsciidocGuide guide = org.getAsciidocGuide(README_REST_ZIPBALL);

        assertThat(guide.getTags(), empty());
        assertThat(guide.getProjects(), empty());
        assertThat(guide.getTableOfContents(), startsWith("<ul class=\"sectlevel1\">"));
        assertThat(guide.getTableOfContents(), not(containsString("<ul class=\"sectlevel2\">")));
    }

    @Test
    public void loadGuideContentWithoutCategoriesOrTableOfContents() throws IOException {
        InputStream inputStream = new DefaultResourceLoader().getResource("classpath:/gs-rest-service-no-tags-no-toc.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();

        given(github.sendRequestForDownload(README_REST_ZIPBALL)).willReturn(zipContents);

        AsciidocGuide guide = org.getAsciidocGuide(README_REST_ZIPBALL);

        assertThat(guide.getTags(), empty());
        assertThat(guide.getProjects(), empty());
        assertThat(guide.getTableOfContents(), equalTo(""));
    }

    @Test
    public void loadGuideWithCategoriesAndProjects() throws IOException {
        InputStream inputStream = new DefaultResourceLoader().getResource("classpath:/gs-rest-service-with-tags-and-projects.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();

        given(github.sendRequestForDownload(README_REST_ZIPBALL)).willReturn(zipContents);

        AsciidocGuide guide = org.getAsciidocGuide(README_REST_ZIPBALL);

        assertThat(guide.getTags(), hasItems("rest", "jackson", "json", "springmvc"));
        assertThat(guide.getProjects(), hasItems("spring-framework"));
        assertThat(guide.getProjects(), not(hasItems("spring-hateoas")));
        assertThat(guide.getTableOfContents(), startsWith("<ul class=\"sectlevel1\">"));
        assertThat(guide.getTableOfContents(), not(containsString("<ul class=\"sectlevel2\">")));
        assertThat(guide.getUnderstandingDocs().keySet(), hasItems("/understanding/JSON", "/understanding/WAR",
                "/understanding/view-templates", "/understanding/application-context", "/understanding/REST",
                "/understanding/Tomcat"));
        assertThat(guide.getUnderstandingDocs().values(), hasItems("JSON", "WAR", "View Technology",
                "Spring Application Context", "RESTful Web Service", "Tomcat"));
    }

}
