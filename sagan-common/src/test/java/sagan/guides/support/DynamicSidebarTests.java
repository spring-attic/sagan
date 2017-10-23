package sagan.guides.support;

import sagan.support.github.GitHubClient;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

@Ignore
public class DynamicSidebarTests {

    private static final String README_REST_ZIPBALL = "/repos/spring-guides/gs-rest-service/zipball";

    @Mock
    GitHubClient github;

    GuideOrganization org;

    @Before
    public void setup() throws IOException {
        initMocks(this);
        org = new GuideOrganization("spring-guides", "orgs", github, new ObjectMapper(), null);
    }

    @Test
    public void loadGuideContentWithCategories() throws IOException {
        InputStream inputStream =
                new DefaultResourceLoader().getResource("classpath:/gs-rest-service-with-tags.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();

        given(github.sendRequestForDownload(README_REST_ZIPBALL)).willReturn(zipContents);

        AsciidocGuide guide = org.getAsciidocGuide(README_REST_ZIPBALL);

        assertThat(guide.getTableOfContents(), startsWith("<ul class=\"sectlevel1\">"));
        assertThat(guide.getTableOfContents(), not(containsString("<ul class=\"sectlevel2\">")));
        assertThat(guide.getContent(), containsString("About 15 minutes"));
    }

    @Test
    public void loadGuideContentWithoutCategories() throws IOException {
        InputStream inputStream =
                new DefaultResourceLoader().getResource("classpath:/gs-rest-service-without-tags.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();

        given(github.sendRequestForDownload(README_REST_ZIPBALL)).willReturn(zipContents);

        AsciidocGuide guide = org.getAsciidocGuide(README_REST_ZIPBALL);

        assertThat(guide.getTableOfContents(), startsWith("<ul class=\"sectlevel1\">"));
        assertThat(guide.getTableOfContents(), not(containsString("<ul class=\"sectlevel2\">")));
        assertThat(guide.getContent(), containsString("About 15 minutes"));
    }

    @Test
    public void loadGuideContentWithoutCategoriesOrTableOfContents() throws IOException {
        InputStream inputStream =
                new DefaultResourceLoader().getResource("classpath:/gs-rest-service-no-tags-no-toc.zip")
                        .getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();

        given(github.sendRequestForDownload(README_REST_ZIPBALL)).willReturn(zipContents);

        AsciidocGuide guide = org.getAsciidocGuide(README_REST_ZIPBALL);

        assertThat(guide.getTableOfContents(), equalTo(""));
        assertThat(guide.getContent(), containsString("About 15 minutes"));
    }

    @Test
    public void loadGuideWithCategoriesAndProjects() throws IOException {
        InputStream inputStream =
                new DefaultResourceLoader().getResource("classpath:/gs-rest-service-with-tags-and-projects.zip")
                        .getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();

        given(github.sendRequestForDownload(README_REST_ZIPBALL)).willReturn(zipContents);

        AsciidocGuide guide = org.getAsciidocGuide(README_REST_ZIPBALL);

        assertThat(guide.getTableOfContents(), startsWith("<ul class=\"sectlevel1\">"));
        assertThat(guide.getTableOfContents(), not(containsString("<ul class=\"sectlevel2\">")));
        assertThat(guide.getContent(), containsString("About 15 minutes"));
    }

}
