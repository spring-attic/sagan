package org.springframework.test.guides;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.site.guides.GettingStartedController;
import org.springframework.site.guides.GitHubGettingStartedService;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.RestClientException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GettingStartedControllerTests {

	private static final String GUIDE_NAME = "rest-service";
	private static final String GUIDE_TEXT = "raw guide text";

	@Mock
	private GitHubGettingStartedService guideService;

	private ExtendedModelMap model;
	private GettingStartedController controller;

	@Before
	public void setup() {
		initMocks(this);
		controller = new GettingStartedController(guideService);
		model = new ExtendedModelMap();
	}

	@Test
	public void guideSlugInModel() {
		when(guideService.loadGuide(GUIDE_NAME)).thenReturn(GUIDE_TEXT);
		controller.viewGuide(GUIDE_NAME, model);
		assertEquals(GUIDE_NAME, model.get("guideSlug"));
	}

	@Test
	public void guideView() {
		when(guideService.loadGuide(GUIDE_NAME)).thenReturn(GUIDE_TEXT);
		assertEquals("guides/gs/guide", controller.viewGuide(GUIDE_NAME, model));
	}

	@Test
	public void guideTextInModel() {
		when(guideService.loadGuide(GUIDE_NAME)).thenReturn(GUIDE_TEXT);
		controller.viewGuide(GUIDE_NAME, model);
		assertEquals(GUIDE_TEXT, model.get("guide"));
	}

	@Test(expected = RestClientException.class)
	public void failedGuideFetch() {
		when(guideService.loadGuide(GUIDE_NAME)).thenThrow(new RestClientException("Is GitHub down?"));
		controller.viewGuide(GUIDE_NAME, model);
	}

}
