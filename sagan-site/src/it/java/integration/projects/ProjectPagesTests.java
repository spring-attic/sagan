package integration.projects;

import org.junit.Test;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import integration.IntegrationTestBase;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectPagesTests extends IntegrationTestBase {

    @Test
    public void getProjectIndex() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/projects")).andExpect(status().isOk());
    }
}
