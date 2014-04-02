package saganx;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthorizationTests extends AbstractIntegrationTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(springSecurityFilterChain).defaultRequest(get("/").secure(true)).build();
    }

    @After
    public void clean() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void actuatorPathsRestricted() throws Exception {
        List<String> actuatorPaths = Arrays.asList("/metrics","/beans","/shutdown","/autoconfig","/env","/mappings","/dump","/configprops","/trace");

        for(String path : actuatorPaths) {
            mockMvc.perform(get(path)).andExpect(status().isUnauthorized());

            mockMvc.perform(get(path + "/")).andExpect(status().is4xxClientError());
            mockMvc.perform(get(path + ".")).andExpect(status().is4xxClientError());
            mockMvc.perform(get(path + ".json")).andExpect(status().is4xxClientError());
        }
    }
}
