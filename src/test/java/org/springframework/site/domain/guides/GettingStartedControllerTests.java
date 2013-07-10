package org.springframework.site.domain.guides;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.site.web.guides.GettingStartedController;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.RestClientException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;

public class GettingStartedControllerTests {

	private static final String GUIDE_NAME = "rest-service";
	private static final String GUIDE_TEXT = "raw guide text";

	@Mock
	private GitHubGettingStartedService guideService;

	private ExtendedModelMap model;
	private GettingStartedController controller;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller = new GettingStartedController(guideService);
		model = new ExtendedModelMap();
	}

	@Test
	public void guideSlugInModel() {
		controller.viewGuide(GUIDE_NAME, model);
		assertThat((String) model.get("guideSlug"), is(GUIDE_NAME));
	}

	@Test
	public void guideView() {
		String view = controller.viewGuide(GUIDE_NAME, model);
		assertThat(view, is("guides/gs/guide"));
	}

	@Test
	public void guideIsInModel() {
		GettingStartedGuide guide = new GettingStartedGuide("guide-id", GUIDE_TEXT, "");
		given(guideService.loadGuide(GUIDE_NAME)).willReturn(guide);
		controller.viewGuide(GUIDE_NAME, model);
		assertThat(((GettingStartedGuide) model.get("guide")), is(guide));
	}

	@Test(expected = RestClientException.class)
	public void failedGuideFetch() {
		given(guideService.loadGuide(GUIDE_NAME)).willThrow(new RestClientException("Is GitHub down?"));
		controller.viewGuide(GUIDE_NAME, model);
	}

	@Test
	public void listGuidesView(){
		String view = controller.listGuides(model);
		assertThat(view, is("guides/gs/list"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void listGuidesModel(){
		List<GuideRepo> repoList = new ArrayList<GuideRepo>();
		given(guideService.listGuides()).willReturn(repoList);
		controller.listGuides(model);
		assertThat((List<GuideRepo>) model.get("guides"), is(repoList));
	}

	@Test
	public void loadImages() {
		byte[] image = "animage".getBytes();
		given(guideService.loadImage(GUIDE_NAME, "welcome.png")).willReturn(image);
		ResponseEntity<byte[]> responseEntity = controller.loadImage(GUIDE_NAME, "welcome.png");
		assertThat(responseEntity.getBody(), is(image));
	}
}
