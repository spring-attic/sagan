package sagan.projects.support;

import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import saganx.AbstractIntegrationTests;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectPageTests extends AbstractIntegrationTests {

    @Test
    public void getProjectPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/project/spring-boot")).andExpect(status().isOk());
    }
}
