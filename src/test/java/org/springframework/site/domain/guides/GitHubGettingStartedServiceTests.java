package org.springframework.site.domain.guides;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.services.GitHubService;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.*;

public class GitHubGettingStartedServiceTests {

	private static final String GUIDE_ID = "rest-service";
	public static final String SIDEBAR = ".*SIDEBAR.md";
	public static final String README = ".*README.md";
	public static final String README_CONTENT = "Getting Started: Building a RESTful Web Service";
	public static final String SIDEBAR_CONTENT = "Related resources";
	@Mock
	private GitHubService gitHubService;

	private GitHubGettingStartedService service;
	private ObjectMapper mapper;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		mapper = new ObjectMapper();
		service = new GitHubGettingStartedService(gitHubService);
	}

	@Test
	public void loadGuideContent() throws IOException {
		given(gitHubService.getRawFileAsHtml(matches(README))).willReturn(README_CONTENT);
		GettingStartedGuide guide = service.loadGuide(GUIDE_ID);
		assertThat(guide.getContent(), equalTo(README_CONTENT));
	}

	@Test
	public void loadGuideSidebar() throws IOException {
		given(gitHubService.getRawFileAsHtml(matches(SIDEBAR))).willReturn(SIDEBAR_CONTENT);
		GettingStartedGuide guide = service.loadGuide(GUIDE_ID);
		assertThat(guide.getSidebar(), is(SIDEBAR_CONTENT));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = GuideNotFoundException.class)
	public void unknownGuide() {
		String unknownGuideId = "foo";
		given(gitHubService.getRawFileAsHtml(anyString())).willThrow(RestClientException.class);
		service.loadGuide(unknownGuideId);
	}

	@Test
	public void guideWithoutSidebar(){
		given(gitHubService.getRawFileAsHtml(matches(README))).willReturn(README_CONTENT);
		given(gitHubService.getRawFileAsHtml(matches(SIDEBAR))).willThrow(RestClientException.class);

		GettingStartedGuide guide = service.loadGuide(GUIDE_ID);

		assertThat(guide.getContent(), is(README_CONTENT));
		assertThat(guide.getSidebar(), isEmptyString());
	}

	@Test
	public void listsGuides() {
		GuideRepo guideRepo = new GuideRepo();
		guideRepo.setName("gs-rest-service");
		GuideRepo notAGuideRepo = new GuideRepo();
		notAGuideRepo.setName("not-a-guide");

		GuideRepo[] guideRepos = {guideRepo, notAGuideRepo};

		given(gitHubService.getForObject(anyString(), eq(GuideRepo[].class))).willReturn(guideRepos);

		assertThat(service.listGuides(), hasItem(guideRepo));
		assertThat(service.listGuides(), not(hasItem(notAGuideRepo)));
	}

	@Test
	public void loadImage() throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> imageResponseFixture = mapper.readValue(new ClassPathResource("gs-device-detection.image.json", getClass()).getInputStream(), Map.class);

		String imageName = "welcome.png";

		given(gitHubService.getForObject(anyString(), eq(Map.class), eq(GUIDE_ID), eq(imageName))).willReturn(imageResponseFixture);

		byte[] result = service.loadImage(GUIDE_ID, imageName);

		assertThat(result, is(notNullValue()));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ImageNotFoundException.class)
	public void unknownImage() {
		String unknownImage = "uknown_image.png";

		given(gitHubService.getForObject(anyString(), eq(Map.class), eq(GUIDE_ID), eq(unknownImage))).willThrow(RestClientException.class);

		service.loadImage(GUIDE_ID, unknownImage);
	}

}
