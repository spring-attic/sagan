package org.springframework.test.guides;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.site.guides.GettingStartedController;
import org.springframework.site.guides.GitHubGettingStartedService;
import org.springframework.site.guides.Guide;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
		controller.viewGuide(GUIDE_NAME, model);
		assertThat((String) model.get("guideSlug"), is(GUIDE_NAME));
	}

	@Test
	public void guideView() {
		String view = controller.viewGuide(GUIDE_NAME, model);
		assertThat(view, is("guides/gs/guide"));
	}

	@Test
	public void guideTextInModel() {
		when(guideService.loadGuide(GUIDE_NAME)).thenReturn(GUIDE_TEXT);
		controller.viewGuide(GUIDE_NAME, model);
		assertThat((String) model.get("guide"), is(GUIDE_TEXT));
	}

	@Test(expected = RestClientException.class)
	public void failedGuideFetch() {
		when(guideService.loadGuide(GUIDE_NAME)).thenThrow(new RestClientException("Is GitHub down?"));
		controller.viewGuide(GUIDE_NAME, model);
	}

	@Test
	public void listGuidesView(){
		String view = controller.listGuides(model);
		assertThat(view, is("guides/gs/list"));
	}

	@Test
	public void listGuidesModel(){
		List<Guide> repoList = new ArrayList<Guide>();
		when(guideService.listGuides()).thenReturn(repoList);
		controller.listGuides(model);
		assertThat((List<Guide>) model.get("guides"), is(repoList));
	}

}
