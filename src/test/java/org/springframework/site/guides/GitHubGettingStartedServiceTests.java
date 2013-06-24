package org.springframework.site.guides;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.services.GitHubService;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GitHubGettingStartedServiceTests {

	private static final String GUIDE_ID = "rest-service";
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
	public void loadGuide() throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> restServiceReadMeFixture = mapper.readValue(new ClassPathResource("gs-rest-service.readme.json", getClass()).getInputStream(), Map.class);
		String guideId = GUIDE_ID;

		when(gitHubService.getForObject(anyString(), eq(Map.class), eq(guideId))).thenReturn(restServiceReadMeFixture);
		when(gitHubService.renderToHtml(anyString())).thenReturn("Getting Started: Building a RESTful Web Service");

		GettingStartedGuide guide = service.loadGuide(guideId);
		assertThat(guide.getContent(), containsString("Getting Started: Building a RESTful Web Service"));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = GuideNotFoundException.class)
	public void unknownGuide() {
		String unknownGuideId = "foo";
		when(gitHubService.getForObject(anyString(), eq(Map.class), eq(unknownGuideId))).thenThrow(RestClientException.class);
		service.loadGuide(unknownGuideId);
	}

	@Test
	public void listsGuides() {
		GuideRepo guideRepo = new GuideRepo();
		guideRepo.setName("gs-rest-service");
		GuideRepo notAGuideRepo = new GuideRepo();
		notAGuideRepo.setName("not-a-guide");

		GuideRepo[] guideRepos = {guideRepo, notAGuideRepo};

		when(gitHubService.getForObject(anyString(), eq(GuideRepo[].class))).thenReturn(guideRepos);

		assertThat(service.listGuides(), hasItem(guideRepo));
		assertThat(service.listGuides(), not(hasItem(notAGuideRepo)));
	}

	@Test
	public void loadImage() throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> imageResponseFixture = mapper.readValue(new ClassPathResource("gs-device-detection.image.json", getClass()).getInputStream(), Map.class);

		String imageName = "welcome.png";

		when(gitHubService.getForObject(anyString(), eq(Map.class), eq(GUIDE_ID), eq(imageName))).thenReturn(imageResponseFixture);

		byte[] result = service.loadImage(GUIDE_ID, imageName);

		assertThat(result, is(notNullValue()));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ImageNotFoundException.class)
	public void unknownImage() {
		String unknownImage = "uknown_image.png";

		when(gitHubService.getForObject(anyString(), eq(Map.class), eq(GUIDE_ID), eq(unknownImage))).thenThrow(RestClientException.class);

		service.loadImage(GUIDE_ID, unknownImage);
	}

}
