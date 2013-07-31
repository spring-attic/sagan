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
		this.controller = new GettingStartedController(this.guideService);
		this.model = new ExtendedModelMap();
	}

	@Test
	public void guideSlugInModel() {
		this.controller.viewGuide("rest-service", this.model);
		assertThat((String) this.model.get("guideSlug"), is("rest-service"));
	}

	@Test
	public void guideView() {
		String view = this.controller.viewGuide("rest-service", this.model);
		assertThat(view, is("guides/gs/guide"));
	}

	@Test
	public void guideViewWithoutContent() {
		String view = this.controller.viewGuide("rest-service");
		assertThat(view, is("redirect:/guides/gs/rest-service/content"));
	}

	@Test
	public void guideIsInModel() {
		GettingStartedGuide guide = new GettingStartedGuide("gs-rest-service",
				"rest-service", "Title :: Description", "raw guide text", "");
		given(this.guideService.loadGuide("rest-service")).willReturn(guide);
		this.controller.viewGuide("rest-service", this.model);
		assertThat(((GettingStartedGuide) this.model.get("guide")), is(guide));
	}

	@Test(expected = RestClientException.class)
	public void failedGuideFetch() {
		given(this.guideService.loadGuide("rest-service")).willThrow(
				new RestClientException("Is GitHub down?"));
		this.controller.viewGuide("rest-service", this.model);
	}

	@Test
	public void listGuidesView() {
		String view = this.controller.listGuides(this.model);
		assertThat(view, is("guides/gs/list"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void listGuidesModel() {
		List<GettingStartedGuide> guideList = new ArrayList<>();
		GettingStartedGuide guide = new GettingStartedGuide("gs-rest-service",
				"rest-service", "Title :: Description", "raw guide text", "");
		guideList.add(guide);

		given(this.guideService.listGuides()).willReturn(guideList);
		this.controller.listGuides(this.model);
		assertThat((List<GettingStartedGuide>) this.model.get("guides"), is(guideList));
	}

	@Test
	public void loadImages() {
		byte[] image = "animage".getBytes();
		given(this.guideService.loadImage("rest-service", "welcome.png")).willReturn(
				image);
		ResponseEntity<byte[]> responseEntity = this.controller.loadImage("rest-service",
				"welcome.png");
		assertThat(responseEntity.getBody(), is(image));
	}
}
