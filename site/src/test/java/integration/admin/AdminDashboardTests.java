package integration.admin;

import integration.IntegrationTestBase;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminDashboardTests extends IntegrationTestBase {

    @Test
    public void adminDashboard() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().isOk());
    }
}
