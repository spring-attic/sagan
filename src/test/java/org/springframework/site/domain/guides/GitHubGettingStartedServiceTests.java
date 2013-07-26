package org.springframework.site.domain.guides;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.services.GitHubService;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.MockitoAnnotations.initMocks;

public class GitHubGettingStartedServiceTests {

	private static final String GUIDE_ID = "rest-service";
	public static final String SIDEBAR = ".*SIDEBAR.md";
	public static final String README = ".*README.md";
	public static final String README_CONTENT = "Getting Started: Building a RESTful Web Service";
	public static final String SIDEBAR_CONTENT = "Related resources";
	private static final GitHubRepo REPO_INFO = new GitHubRepo();

	@Mock
	private GitHubService gitHubService;

	private GitHubGettingStartedService service;
	private ObjectMapper mapper;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		this.mapper = new ObjectMapper();
		this.service = new GitHubGettingStartedService(this.gitHubService);
	}

	@Test
	public void loadGuideContent() throws IOException {
		given(this.gitHubService.getRawFileAsHtml(matches(README))).willReturn(README_CONTENT);
		given(this.gitHubService.getRepoInfo(anyString(), anyString())).willReturn(REPO_INFO);
		GettingStartedGuide guide = this.service.loadGuide(GUIDE_ID);
		assertThat(guide.getContent(), equalTo(README_CONTENT));
	}

	@Test
	public void loadGuideSetsDescription() throws IOException {
		String description = "Awesome Guide :: Learn awesome stuff with this guide";
		REPO_INFO.setDescription(description);
		given(this.gitHubService.getRepoInfo("springframework-meta", GUIDE_ID)).willReturn(REPO_INFO);
		GettingStartedGuide guide = this.service.loadGuide(GUIDE_ID);
		assertThat(guide.getDescription(), equalTo(description));
	}

	@Test
	public void loadGuideSidebar() throws IOException {
		given(this.gitHubService.getRawFileAsHtml(matches(SIDEBAR))).willReturn(SIDEBAR_CONTENT);
		given(this.gitHubService.getRepoInfo(anyString(), anyString())).willReturn(REPO_INFO);
		GettingStartedGuide guide = this.service.loadGuide(GUIDE_ID);
		assertThat(guide.getSidebar(), is(SIDEBAR_CONTENT));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = GuideNotFoundException.class)
	public void unknownGuide() {
		String unknownGuideId = "foo";
		given(this.gitHubService.getRepoInfo(anyString(), anyString())).willThrow(RestClientException.class);
		given(this.gitHubService.getRawFileAsHtml(anyString())).willThrow(RestClientException.class);
		this.service.loadGuide(unknownGuideId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void guideWithoutSidebar() {
		given(this.gitHubService.getRawFileAsHtml(matches(README))).willReturn(README_CONTENT);
		given(this.gitHubService.getRawFileAsHtml(matches(SIDEBAR))).willThrow(RestClientException.class);
		given(this.gitHubService.getRepoInfo(anyString(), anyString())).willReturn(REPO_INFO);

		GettingStartedGuide guide = this.service.loadGuide(GUIDE_ID);

		assertThat(guide.getContent(), is(README_CONTENT));
		assertThat(guide.getSidebar(), isEmptyString());
	}

	@Test
	public void listsGuides() {
		GuideRepo guideRepo = new GuideRepo();
		guideRepo.setName("gs-rest-service");
		GuideRepo notAGuideRepo = new GuideRepo();
		notAGuideRepo.setName("not-a-guide");

		GuideRepo[] guideRepos = { guideRepo, notAGuideRepo };

		given(this.gitHubService.getForObject(anyString(), eq(GuideRepo[].class)))
				.willReturn(guideRepos);

		assertThat(this.service.listGuides(), hasItem(guideRepo));
		assertThat(this.service.listGuides(), not(hasItem(notAGuideRepo)));
	}

	@Test
	public void loadImage() throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> imageResponseFixture = this.mapper.readValue(
				new ClassPathResource("gs-device-detection.image.json", getClass())
						.getInputStream(), Map.class);

		String imageName = "welcome.png";

		given(
				this.gitHubService.getForObject(anyString(), eq(Map.class), eq(GUIDE_ID),
						eq(imageName))).willReturn(imageResponseFixture);

		byte[] result = this.service.loadImage(GUIDE_ID, imageName);

		assertThat(result, is(notNullValue()));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ImageNotFoundException.class)
	public void unknownImage() {
		String unknownImage = "uknown_image.png";

		given(
				this.gitHubService.getForObject(anyString(), eq(Map.class), eq(GUIDE_ID),
						eq(unknownImage))).willThrow(RestClientException.class);

		this.service.loadImage(GUIDE_ID, unknownImage);
	}

}
