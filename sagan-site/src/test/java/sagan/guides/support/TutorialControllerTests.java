package sagan.guides.support;

import sagan.guides.DefaultGuideMetadata;
import sagan.guides.Tutorial;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

public class TutorialControllerTests {

    @Mock
    private Tutorials guides;
    private Tutorial guide;
    private TutorialController controller;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new TutorialController(guides);
        guide = new Tutorial(new DefaultGuideMetadata("my-org", "rest", "tut-rest", ""), guides, guides);
    }

    @Test
    public void loadImage() {
        byte[] image = "animage".getBytes();
        given(guides.find(anyString())).willReturn(guide);
        given(guides.loadImage(guide, "welcome.png")).willReturn(image);
        ResponseEntity<byte[]> responseEntity = controller.loadImage("rest", "welcome.png");
        assertThat(responseEntity.getBody(), is(image));
    }

}
