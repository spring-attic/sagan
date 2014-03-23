package sagan.guides.support;

import sagan.guides.DefaultGuideMetadata;
import sagan.guides.GettingStartedGuide;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.RestClientException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

/**
 * Unit tests for {@link GettingStartedGuideController}.
 */
public class GettingStartedGuideControllerTests {

    @Mock
    private GettingStartedGuides guides;
    private GettingStartedGuide guide;
    private ExtendedModelMap model;
    private GettingStartedGuideController controller;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new GettingStartedGuideController(guides);
        model = new ExtendedModelMap();
        guide = new GettingStartedGuide(
                new DefaultGuideMetadata("my-org", "rest-service", "gs-rest-service", ""), guides, guides);
    }

    @Test
    public void viewGuide() {
        String view = controller.viewGuide("rest-service", model);
        assertThat(view, is("guides/gs/guide"));
    }

    @Test
    public void guideIsInModel() {
        given(guides.find("rest-service")).willReturn(guide);
        controller.viewGuide("rest-service", model);
        assertThat(((GettingStartedGuide) model.get("guide")), is(guide));
    }

    @Test(expected = RestClientException.class)
    public void viewGuide_fails() {
        given(guides.find("rest-service")).willThrow(new RestClientException("Is GitHub down?"));
        controller.viewGuide("rest-service", model);
    }

    @Test
    public void loadImage() {
        byte[] image = "animage".getBytes();
        given(guides.find(anyString())).willReturn(guide);
        given(guides.loadImage(guide, "welcome.png")).willReturn(image);
        ResponseEntity<byte[]> responseEntity = controller.loadImage("rest-service", "welcome.png");
        assertThat(responseEntity.getBody(), is(image));
    }

}
