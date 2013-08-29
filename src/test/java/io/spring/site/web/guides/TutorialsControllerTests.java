package io.spring.site.web.guides;

import io.spring.site.domain.guides.GitHubGuidesService;
import io.spring.site.web.guides.TutorialsController;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

public class TutorialsControllerTests {

	@Mock
	private GitHubGuidesService guideService;

	private TutorialsController controller;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.controller = new TutorialsController(this.guideService);
	}

	@Test
	public void loadImages() {
		byte[] image = "animage".getBytes();
		given(this.guideService.loadTutorialImage("rest-service", "welcome.png")).willReturn(
				image);
		ResponseEntity<byte[]> responseEntity = this.controller.loadImage("rest-service",
				"welcome.png");
		assertThat(responseEntity.getBody(), is(image));
	}

}
