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
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GitHubGettingStartedServiceTests {

	@Mock
	private GitHub gh;

	@Mock
	private RestOperations restOperations;

	private Map<String, String> restServiceReadMeFixture;
	private GitHubGettingStartedService service;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		when(gh.restOperations()).thenReturn(restOperations);

		ObjectMapper mapper = new ObjectMapper();
		restServiceReadMeFixture = mapper.readValue(new ClassPathResource("gs-rest-service.readme.json", getClass()).getInputStream(), Map.class);
		service = new GitHubGettingStartedService(gh);
	}

	@Test
	public void loadGuide() {
		String guideId = "rest-service";
		when(restOperations.getForObject(anyString(), eq(Map.class), eq(guideId))).thenReturn(restServiceReadMeFixture);
		String guide = service.loadGuide(guideId);
		assertTrue(guide.contains("Getting Started: Building a RESTful Web Service"));
	}

	@Test(expected = GuideNotFoundException.class)
	public void unknownGuide() {
		String unknownGuideId = "foo";
		when(restOperations.getForObject(anyString(), eq(Map.class), eq(unknownGuideId))).thenThrow(GuideNotFoundException.class);
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

}
