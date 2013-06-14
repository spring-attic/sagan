package org.springframework.test.guides;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.guides.GitHubGettingStartedService;
import org.springframework.site.guides.Guide;
import org.springframework.site.guides.GuideNotFoundException;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GitHubGettingStartedServiceTests {

	private static final String GUIDE_ID = "rest-service";
	@Mock
	private GitHub gh;

	@Mock
	private RestOperations restOperations;

	private GitHubGettingStartedService service;
	private ObjectMapper mapper;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		when(gh.restOperations()).thenReturn(restOperations);

		mapper = new ObjectMapper();
		service = new GitHubGettingStartedService(gh);
	}

	@Test
	public void loadGuide() throws IOException {
		Map<String, String> restServiceReadMeFixture = mapper.readValue(new ClassPathResource("gs-rest-service.readme.json", getClass()).getInputStream(), Map.class);
		String guideId = GUIDE_ID;

		when(restOperations.getForObject(anyString(), eq(Map.class), eq(guideId))).thenReturn(restServiceReadMeFixture);
		when(restOperations.postForObject(anyString(), anyString(), eq(String.class))).thenReturn("Getting Started: Building a RESTful Web Service");

		String guide = service.loadGuide(guideId);
		assertThat(guide, containsString("Getting Started: Building a RESTful Web Service"));
	}

	@Test(expected = GuideNotFoundException.class)
	public void unknownGuide() {
		String unknownGuideId = "foo";
		when(restOperations.getForObject(anyString(), eq(Map.class), eq(unknownGuideId))).thenThrow(RestClientException.class);
		service.loadGuide(unknownGuideId);
	}

	@Test
	public void listsGuides() {
		Guide guide = new Guide();
		guide.setName("gs-rest-service");
		Guide notAGuide = new Guide();
		notAGuide.setName("not-a-guide");

		Guide[] guides = {guide, notAGuide};

		when(restOperations.getForObject(anyString(), eq(Guide[].class))).thenReturn(guides);

		assertThat(service.listGuides(), hasItem(guide));
		assertThat(service.listGuides(), not(hasItem(notAGuide)));
	}

	@Test
	public void loadImage() throws IOException {
		Map<String, String> imageResponseFixture = mapper.readValue(new ClassPathResource("gs-device-detection.image.json", getClass()).getInputStream(), Map.class);

		String imageName = "welcome.png";

		when(restOperations.getForObject(anyString(), eq(Map.class), eq(GUIDE_ID), eq(imageName))).thenReturn(imageResponseFixture);

		byte[] result = service.loadImage(GUIDE_ID, imageName);

		assertNotNull(result);
	}

	@Test(expected = ImageNotFoundException.class)
	public void unknownImage() {
		String unknownImage = "uknown_image.png";

		when(restOperations.getForObject(anyString(), eq(Map.class), eq(GUIDE_ID), eq(unknownImage))).thenThrow(RestClientException.class);

		service.loadImage(GUIDE_ID, unknownImage);
	}

}
