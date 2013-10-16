package integration.guides;

import integration.IntegrationTestBase;

import org.junit.Test;
import utils.FixtureLoader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TutorialsTests extends IntegrationTestBase {

    @Test
    public void getTutorialRootPage() throws Exception {
        stubRestClient.putResponse("/repos/spring-guides/tut-my-tutorial/contents/README.md",
                "html");

        this.mockMvc.perform(get("/guides/tutorials/my-tutorial"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));

    }

    @Test
    public void getTutorialPage1() throws Exception {
        stubRestClient.putResponse("/repos/spring-guides/tut-my-tutorial/contents/1/README.md",
                "html");

        this.mockMvc.perform(get("/guides/tutorials/my-tutorial/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));

    }

    @Test
    public void getImage() throws Exception {
        String imageJson = FixtureLoader.load("/fixtures/github/imageResponse.json");

        stubRestClient.putResponse("/repos/spring-guides/tut-my-tutorial/contents/images/image.png",
                imageJson);


        this.mockMvc.perform(get("/guides/tutorials/my-tutorial/images/image.png"))
                .andExpect(status().isOk());

    }
}
