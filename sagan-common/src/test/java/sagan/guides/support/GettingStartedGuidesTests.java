package sagan.guides.support;

import sagan.guides.GettingStartedGuide;
import sagan.guides.Guide;
import sagan.projects.service.ProjectMetadataService;
import sagan.util.ResourceNotFoundException;
import sagan.util.service.github.Readme;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;

import org.springframework.social.github.api.GitHubRepo;
import org.springframework.web.client.RestClientException;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link GettingStartedGuides}.
 * 
 * @author Chris Beams
 * @author Greg Turnquist
 */
public class GettingStartedGuidesTests {

    private static final String GUIDE_ID = "rest-service";
    private static final String GUIDE_REPO_NAME = "gs-rest-service";
    public static final String SIDEBAR = ".*SIDEBAR.md";
    public static final String README = ".*README.md";
    public static final String README_CONTENT = "Getting Started: Building a RESTful Web Service";
    public static final String SIDEBAR_CONTENT = "Related resources";
    private static final GitHubRepo REPO_INFO = new GitHubRepo();

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private GuideOrganization org;

    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private Guide gsRestService;

    private GettingStartedGuides gsGuides;
    private Readme readme = new Readme();

    @Before
    public void setup() throws IOException {
        initMocks(this);
        given(org.getName()).willReturn("mock-org");

        gsGuides = new GettingStartedGuides(org, projectMetadataService);
        readme.setName("README.md");

        given(gsRestService.getRepoName()).willReturn(GUIDE_REPO_NAME);
    }

    @Test
    public void loadGuideContent() throws IOException {
        given(org.getReadme(anyString())).willReturn(readme);
        given(org.getMarkdownFileAsHtml(matches(README))).willReturn(README_CONTENT);
        given(org.getRepoInfo(anyString())).willReturn(REPO_INFO);
        String description = "Awesome Guide :: Learn awesome stuff with this guide";
        REPO_INFO.setDescription(description);
        GettingStartedGuide guide = gsGuides.find(GUIDE_ID);
        assertThat(guide.getContent(), equalTo(README_CONTENT));
    }

    @Test
    public void loadGuideTitle() throws IOException {
        String description = "Awesome Guide :: Learn awesome stuff with this guide";
        REPO_INFO.setDescription(description);
        given(org.getReadme(anyString())).willReturn(readme);
        given(org.getRepoInfo(GUIDE_REPO_NAME)).willReturn(REPO_INFO);
        GettingStartedGuide guide = gsGuides.find(GUIDE_ID);
        assertThat(guide.getTitle(), equalTo("Awesome Guide"));
    }

    @Test
    public void loadGuideSubtitle() throws IOException {
        String description = "Awesome Guide :: Learn awesome stuff with this guide";
        REPO_INFO.setDescription(description);
        given(org.getReadme(anyString())).willReturn(readme);
        given(org.getRepoInfo(GUIDE_REPO_NAME)).willReturn(REPO_INFO);
        GettingStartedGuide guide = gsGuides.find(GUIDE_ID);
        assertThat(guide.getSubtitle(), equalTo("Learn awesome stuff with this guide"));
    }

    @Test
    public void loadGuideSidebar() throws IOException {
        given(org.getReadme(anyString())).willReturn(readme);
        given(org.getMarkdownFileAsHtml(matches(SIDEBAR))).willReturn(SIDEBAR_CONTENT);
        given(org.getRepoInfo(anyString())).willReturn(REPO_INFO);
        GettingStartedGuide guide = gsGuides.find(GUIDE_ID);
        assertThat(guide.getSidebar(), is("<div class='right-pane-widget--container'>\n" +
                "<div class='related_resources'>\n" +
                SIDEBAR_CONTENT + "</div>\n" +
                "</div>"));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ResourceNotFoundException.class)
    public void unknownGuide() {
        String unknownGuideId = "foo";
        given(org.getReadme(anyString())).willThrow(RestClientException.class);
        given(org.getRepoInfo(anyString())).willThrow(RestClientException.class);
        given(org.getMarkdownFileAsHtml(anyString())).willThrow(RestClientException.class);
        GettingStartedGuide unknownGuide = gsGuides.find(unknownGuideId);
        unknownGuide.getContent(); // should throw
    }

    @SuppressWarnings("unchecked")
    @Test
    public void guideWithoutSidebar() {
        given(org.getReadme(anyString())).willReturn(readme);
        given(org.getMarkdownFileAsHtml(matches(README))).willReturn(README_CONTENT);
        given(org.getMarkdownFileAsHtml(matches(SIDEBAR))).willThrow(RestClientException.class);
        given(org.getRepoInfo(anyString())).willReturn(REPO_INFO);

        GettingStartedGuide guide = gsGuides.find(GUIDE_ID);

        assertThat(guide.getContent(), is(README_CONTENT));
        assertThat(guide.getSidebar(), isEmptyString());
    }

    @Test
    public void listAll() {
        GitHubRepo repo = new GitHubRepo();
        repo.setName("gs-rest-service");
        repo.setDescription("Awesome Guide :: Learn awesome stuff with this guide");

        given(org.findRepositoriesByPrefix(GettingStartedGuides.REPO_PREFIX)).willReturn(singletonList(repo));
        given(org.getMarkdownFileAsHtml(matches(README))).willReturn(README_CONTENT);
        given(org.getMarkdownFileAsHtml(matches(SIDEBAR))).willReturn(SIDEBAR_CONTENT);

        List<GettingStartedGuide> guides = gsGuides.findAll();
        assertThat(guides.size(), is(1));
        assertThat(guides.get(0).getGuideId(), equalTo("rest-service"));
        assertThat(guides.get(0).getTitle(), equalTo("Awesome Guide"));
        assertThat(guides.get(0).getSubtitle(), equalTo("Learn awesome stuff with this guide"));
    }

    @Test
    public void loadImage() throws IOException {
        byte[] bytes = new byte[] { 'a' };
        String imageName = "welcome.png";

        GitHubRepo dummyRepo = new GitHubRepo();
        dummyRepo.setDescription("dummy");
        given(org.getRepoInfo(anyString())).willReturn(dummyRepo);

        given(org.getGuideImage(eq(GUIDE_REPO_NAME), eq(imageName))).willReturn(bytes);

        byte[] result = gsGuides.loadImage(gsRestService, imageName);

        assertThat(result, equalTo(bytes));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ResourceNotFoundException.class)
    public void unknownImage() {
        String unknownImage = "uknown_image.png";

        GitHubRepo dummyRepo = new GitHubRepo();
        dummyRepo.setDescription("dummy");
        given(org.getRepoInfo(anyString())).willReturn(dummyRepo);

        given(org.getGuideImage(eq(GUIDE_REPO_NAME), eq(unknownImage))).willThrow(RestClientException.class);

        gsGuides.loadImage(gsRestService, unknownImage);
    }

}
