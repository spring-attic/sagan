package integration.projects;

import integration.IntegrationTestBase;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectPagesTests extends IntegrationTestBase {

    @Test
    public void getProjectIndex() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/projects")).andExpect(status().isOk());
    }
}
