package sagan.admin.support;

import saganx.AbstractIntegrationTests;

import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminDashboardTests extends AbstractIntegrationTests {

    @Test
    public void adminDashboard() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().isOk());
    }
}
