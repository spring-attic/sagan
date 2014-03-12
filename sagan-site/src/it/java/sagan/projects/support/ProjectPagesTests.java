package sagan.projects.support;

import saganx.AbstractIntegrationTests;

import org.junit.Test;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectPagesTests extends AbstractIntegrationTests {

    @Test
    public void getProjectIndex() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/projects")).andExpect(status().isOk());
    }
}
