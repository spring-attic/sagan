package org.springframework.site.domain.guides;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.site.domain.services.github.GitHubService;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class GitHubGuidesServiceTests {

	private static final String GUIDE_ID = "rest-service";
	private static final String GUIDE_REPO_NAME = "gs-rest-service";
	public static final String SIDEBAR = ".*SIDEBAR.md";
	public static final String README = ".*README.md";
	public static final String README_CONTENT = "Getting Started: Building a RESTful Web Service";
	public static final String SIDEBAR_CONTENT = "Related resources";
	private static final GitHubRepo REPO_INFO = new GitHubRepo();

	@Mock
	private GitHubService gitHubService;

	private GitHubGuidesService service;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		this.service = new GitHubGuidesService(this.gitHubService);
	}

	@Test
	public void loadGuideContent() throws IOException {
		given(this.gitHubService.getRawFileAsHtml(matches(README))).willReturn(README_CONTENT);
		given(this.gitHubService.getRepoInfo(anyString(), anyString())).willReturn(REPO_INFO);
		String description = "Awesome Guide :: Learn awesome stuff with this guide";
		REPO_INFO.setDescription(description);
		Guide guide = this.service.loadGettingStartedGuide(GUIDE_ID);
		assertThat(guide.getContent(), equalTo(README_CONTENT));
	}

	@Test
	public void loadGuideTitle() throws IOException {
		String description = "Awesome Guide :: Learn awesome stuff with this guide";
		REPO_INFO.setDescription(description);
		given(this.gitHubService.getRepoInfo("springframework-meta", GUIDE_REPO_NAME)).willReturn(REPO_INFO);
		Guide guide = this.service.loadGettingStartedGuide(GUIDE_ID);
		assertThat(guide.getTitle(), equalTo("Awesome Guide"));
	}

	@Test
	public void loadGuideSubTitle() throws IOException {
		String description = "Awesome Guide :: Learn awesome stuff with this guide";
		REPO_INFO.setDescription(description);
		given(this.gitHubService.getRepoInfo("springframework-meta", GUIDE_REPO_NAME)).willReturn(REPO_INFO);
		Guide guide = this.service.loadGettingStartedGuide(GUIDE_ID);
		assertThat(guide.getSubtitle(), equalTo("Learn awesome stuff with this guide"));
	}

	@Test
	public void loadGuideSidebar() throws IOException {
		given(this.gitHubService.getRawFileAsHtml(matches(SIDEBAR))).willReturn(SIDEBAR_CONTENT);
		given(this.gitHubService.getRepoInfo(anyString(), anyString())).willReturn(REPO_INFO);
		Guide guide = this.service.loadGettingStartedGuide(GUIDE_ID);
		assertThat(guide.getSidebar(), is(SIDEBAR_CONTENT));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = GuideNotFoundException.class)
	public void unknownGuide() {
		String unknownGuideId = "foo";
		given(this.gitHubService.getRepoInfo(anyString(), anyString())).willThrow(RestClientException.class);
		given(this.gitHubService.getRawFileAsHtml(anyString())).willThrow(RestClientException.class);
		this.service.loadGettingStartedGuide(unknownGuideId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void guideWithoutSidebar() {
		given(this.gitHubService.getRawFileAsHtml(matches(README))).willReturn(README_CONTENT);
		given(this.gitHubService.getRawFileAsHtml(matches(SIDEBAR))).willThrow(RestClientException.class);
		given(this.gitHubService.getRepoInfo(anyString(), anyString())).willReturn(REPO_INFO);

		Guide guide = this.service.loadGettingStartedGuide(GUIDE_ID);

		assertThat(guide.getContent(), is(README_CONTENT));
		assertThat(guide.getSidebar(), isEmptyString());
	}

	@Test
	public void listsGuides() {
		GitHubRepo guideRepo = new GitHubRepo();
		guideRepo.setName("gs-rest-service");
		guideRepo.setDescription("Awesome Guide :: Learn awesome stuff with this guide");

		GitHubRepo notAGuideRepo = new GitHubRepo();
		notAGuideRepo.setName("not-a-guide");

		GitHubRepo[] guideRepos = {guideRepo, notAGuideRepo};

		given(this.gitHubService.getGitHubRepos(anyString())).willReturn(guideRepos);

		List<Guide> guides = this.service.listGettingStartedGuides();
		assertThat(guides.size(), is(1));
		assertThat(guides.get(0).getGuideId(), equalTo("rest-service"));
	}

	@Test
	public void listsTutorials() {
		GitHubRepo tutorialRepo = new GitHubRepo();
		tutorialRepo.setName("tut-rest");
		tutorialRepo.setDescription("Rest tutorial :: Learn some rest stuff");
		GitHubRepo notAGuideRepo = new GitHubRepo();
		notAGuideRepo.setName("gs-not-a-tutorial");

		GitHubRepo[] guideRepos = {tutorialRepo, notAGuideRepo};

		given(this.gitHubService.getGitHubRepos(anyString())).willReturn(guideRepos);

		List<Guide> tutorials = this.service.listTutorials();
		assertThat(tutorials.size(), is(1));
		assertThat(tutorials.get(0).getGuideId(), equalTo("rest"));
	}

	@Test
	public void loadImage() throws IOException {
		byte[] bytes = new byte[]{'a'};
		String imageName = "welcome.png";

		given(this.gitHubService.getGuideImage(eq(GUIDE_REPO_NAME), eq(imageName))).willReturn(bytes);

		byte[] result = this.service.loadGettingStartedImage(GUIDE_ID, imageName);

		assertThat(result, equalTo(bytes));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ImageNotFoundException.class)
	public void unknownImage() {
		String unknownImage = "uknown_image.png";

		given(
            this.gitHubService.getGuideImage(eq(GUIDE_REPO_NAME), eq(unknownImage))
		).willThrow(RestClientException.class);

		this.service.loadGettingStartedImage(GUIDE_ID, unknownImage);
	}

	@Test
	public void loadTutorialRootPageContent() throws IOException {
		GitHubRepo gitHubRepo = new GitHubRepo();
		gitHubRepo.setUrl("/path/to/tutorial");
		gitHubRepo.setDescription("Rest tutorial :: Learn some rest stuff");

		given(this.gitHubService.getRepoInfo(anyString(), eq("tut-tutorialId"))).willReturn(gitHubRepo);
		given(this.gitHubService.getRawFileAsHtml(matches("/repos/springframework-meta/tut-tutorialId/contents/README.md"))).willReturn("Tutorial Page 1");

		Guide guide = this.service.loadTutorial("tutorialId");
		verify(this.gitHubService).getRawFileAsHtml(matches("/repos/springframework-meta/tut-tutorialId/contents/README.md"));

		assertThat(guide.getContent(), equalTo("Tutorial Page 1"));
	}

	@Test
	public void loadTutorialPage1Content() throws IOException {
		GitHubRepo gitHubRepo = new GitHubRepo();
		gitHubRepo.setUrl("/path/to/tutorial");
		gitHubRepo.setDescription("Rest tutorial :: Learn some rest stuff");

		given(this.gitHubService.getRepoInfo(anyString(), eq("tut-tutorialId"))).willReturn(gitHubRepo);
		given(this.gitHubService.getRawFileAsHtml(matches("/repos/springframework-meta/tut-tutorialId/contents/1/README.md"))).willReturn("Tutorial Page 1");

		Guide guide = this.service.loadTutorialPage("tutorialId", "1");
		verify(this.gitHubService).getRawFileAsHtml(matches("/repos/springframework-meta/tut-tutorialId/contents/1/README.md"));

		assertThat(guide.getContent(), equalTo("Tutorial Page 1"));
	}

}
