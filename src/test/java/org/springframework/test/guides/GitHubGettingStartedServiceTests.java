package org.springframework.test.guides;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.guides.GitHubGettingStartedService;
import org.springframework.site.guides.GuideNotFoundException;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GitHubGettingStartedServiceTests {

	@Mock
	private GitHub gh;

	@Mock
	private RestOperations ghOperations;

	private Map<String, String> restServiceReadMeFixture;
	private GitHubGettingStartedService service;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		when(gh.restOperations()).thenReturn(ghOperations);
		ObjectMapper mapper = new ObjectMapper();
		restServiceReadMeFixture = mapper.readValue(new ClassPathResource("gs-rest-service.readme.json", getClass()).getInputStream(), Map.class);
		service = new GitHubGettingStartedService(gh);
	}

	@Test
	public void loadGuide() {
		String guideId = "rest-service";
		when(ghOperations.getForObject(anyString(), eq(Map.class), eq(guideId))).thenReturn(restServiceReadMeFixture);
		String guide = service.loadGuide(guideId);
		assertTrue(guide.contains("Getting Started: Building a RESTful Web Service"));
	}

	@Test(expected = GuideNotFoundException.class)
	public void unknownGuide() {
		String unknownGuideId = "foo";
		when(ghOperations.getForObject(anyString(), eq(Map.class), eq(unknownGuideId))).thenThrow(GuideNotFoundException.class);
		service.loadGuide(unknownGuideId);
	}

}
