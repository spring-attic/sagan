package org.springframework.site.domain.guides;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.site.web.guides.GettingStartedController;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

public class GettingStartedControllerTests {

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
		controller.viewGuide("rest-service", model);
		assertThat((String) model.get("guideSlug"), is("rest-service"));
	}

	@Test
	public void guideView() {
		String view = controller.viewGuide("rest-service", model);
		assertThat(view, is("guides/gs/guide"));
	}

	@Test
	public void guideIsInModel() {
		GettingStartedGuide guide = new GettingStartedGuide("gs-rest-service", "rest-service", "Title :: Description", "raw guide text", "");
		given(guideService.loadGuide("rest-service")).willReturn(guide);
		controller.viewGuide("rest-service", model);
		assertThat(((GettingStartedGuide) model.get("guide")), is(guide));
	}

	@Test(expected = RestClientException.class)
	public void failedGuideFetch() {
		given(guideService.loadGuide("rest-service")).willThrow(new RestClientException("Is GitHub down?"));
		controller.viewGuide("rest-service", model);
	}

	@Test
	public void listGuidesView(){
		String view = controller.listGuides(model);
		assertThat(view, is("guides/gs/list"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void listGuidesModel(){
		List<GettingStartedGuide> guideList = new ArrayList<>();
		GettingStartedGuide guide = new GettingStartedGuide("gs-rest-service", "rest-service", "Title :: Description", "raw guide text", "");
		guideList.add(guide);

		given(guideService.listGuides()).willReturn(guideList);
		controller.listGuides(model);
		assertThat((List<GettingStartedGuide>) model.get("guides"), is(guideList));
	}

	@Test
	public void loadImages() {
		byte[] image = "animage".getBytes();
		given(guideService.loadImage("rest-service", "welcome.png")).willReturn(image);
		ResponseEntity<byte[]> responseEntity = controller.loadImage("rest-service", "welcome.png");
		assertThat(responseEntity.getBody(), is(image));
	}
}
