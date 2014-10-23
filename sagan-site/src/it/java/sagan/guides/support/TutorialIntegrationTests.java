package sagan.guides.support;

import org.junit.Test;
import sagan.support.Fixtures;
import saganx.AbstractIntegrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TutorialIntegrationTests extends AbstractIntegrationTests {

    @Test
    public void getImage() throws Exception {
        String imageJson = Fixtures.load("/fixtures/github/imageResponse.json");

        stubRestClient.putResponse("/repos/spring-guides/tut-my-tutorial/contents/images/image.png", imageJson);

        mockMvc.perform(get("/guides/tutorials/my-tutorial/images/image.png")).andExpect(status().isOk());

    }

}
