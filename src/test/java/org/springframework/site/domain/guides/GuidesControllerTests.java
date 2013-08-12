package org.springframework.site.domain.guides;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.site.web.guides.GuidesController;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

public class GuidesControllerTests {

	@Mock
	private GitHubGuidesService guideService;

	private ExtendedModelMap model;
	private GuidesController controller;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.controller = new GuidesController(this.guideService);
		this.model = new ExtendedModelMap();
	}

	@Test
	public void listGuidesView() {
		String view = this.controller.index(this.model);
		assertThat(view, is("guides/index"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void gettingStartedGuidesAreInModel() {
		List<Guide> guideList = new ArrayList<>();
		Guide guide = new Guide("gs-rest-service",
				"rest-service", "Title :: Description", "raw guide text", "");
		guideList.add(guide);

		given(this.guideService.listGettingStartedGuides()).willReturn(guideList);
		this.controller.index(this.model);
		assertThat((List<Guide>) this.model.get("guides"), is(guideList));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void tutorialsAreInModel() {
		List<Guide> tutorialsList = new ArrayList<>();
		Guide guide = new Guide("tut-rest", "rest", "Title :: Description", "raw guide text", "");
		tutorialsList.add(guide);

		given(this.guideService.listTutorials()).willReturn(tutorialsList);
		this.controller.index(this.model);
		assertThat((List<Guide>) this.model.get("tutorials"), is(tutorialsList));
	}
}
