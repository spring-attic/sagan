package sagan.guides.support;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.web.client.RestClientException;
import sagan.guides.GettingStartedGuide;
import sagan.guides.Guide;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;
import sagan.support.github.Readme;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link GettingStartedGuides}.
 */
public class GettingStartedGuidesTests {

    private static final String GUIDE_ID = "rest-service";
    private static final String GUIDE_REPO_NAME = "gs-rest-service";
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
    public void loadGuideTitle() throws IOException {
        String description = "Awesome Guide :: Learn awesome stuff with this guide";
        REPO_INFO.setDescription(description);
        given(org.getReadme(anyString())).willReturn(readme);
        given(org.getRepoInfo(GUIDE_REPO_NAME)).willReturn(REPO_INFO);
        given(org.getAsciidocGuide("/repos/mock-org/gs-rest-service/zipball")).willReturn(
                new AsciidocGuide("Awesome Guide", "Table of C"));
        GettingStartedGuide guide = gsGuides.find(GUIDE_ID);
        assertThat(guide.getTitle(), equalTo("Awesome Guide"));
    }

    @Test
    public void loadGuideSubtitle() throws IOException {
        String description = "Awesome Guide :: Learn awesome stuff with this guide";
        REPO_INFO.setDescription(description);
        given(org.getReadme(anyString())).willReturn(readme);
        given(org.getRepoInfo(GUIDE_REPO_NAME)).willReturn(REPO_INFO);
        given(org.getAsciidocGuide("/repos/mock-org/gs-rest-service/zipball")).willReturn(
                new AsciidocGuide("Awesome Guide", "Table of C"));
        GettingStartedGuide guide = gsGuides.find(GUIDE_ID);
        assertThat(guide.getSubtitle(), equalTo("Learn awesome stuff with this guide"));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ResourceNotFoundException.class)
    public void unknownGuide() {
        String unknownGuideId = "foo";
        given(org.getRepoInfo(anyString())).willThrow(ResourceNotFoundException.class);
        given(org.getAsciidocGuide(anyString())).willThrow(ResourceNotFoundException.class);
        GettingStartedGuide unknownGuide = gsGuides.find(unknownGuideId);
        unknownGuide.getContent(); // should throw
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

    @Test
    public void testParseGuideName() throws Exception {
        String guideName = gsGuides.parseGuideName("gs-guide-name");
        assertThat(guideName, is("guide-name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseEmptyGuideName() throws Exception {
        gsGuides.parseGuideName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidGuideName() throws Exception {
        gsGuides.parseGuideName("invalid");
    }

}
