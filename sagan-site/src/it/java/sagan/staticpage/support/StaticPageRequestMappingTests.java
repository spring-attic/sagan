package sagan.staticpage.support;

import saganx.AbstractIntegrationTests;

import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class StaticPageRequestMappingTests extends AbstractIntegrationTests {

    @Test
    public void getHomePage() throws Exception {
        checkPage("/");
    }

    @Test
    public void getServicesPage() throws Exception {
        checkPage("/services");
    }

    @Test
    public void getSigninPage() throws Exception {
        checkPage("/signin");
    }

    private void checkPage(String page) throws Exception {
        mockMvc.perform(get(page))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void getAStaticPageWithSlashAtEnd() throws Exception {
        checkPage("/services/");
    }

    @Test
    public void getRobotsFile() throws Exception {
        mockMvc.perform(get("/robots.txt"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User-agent")));
    }

    @Test
    public void doesNotGetIndexPage() throws Exception {
        mockMvc.perform(get("/index"))
                .andExpect(status().isNotFound());
    }

}
