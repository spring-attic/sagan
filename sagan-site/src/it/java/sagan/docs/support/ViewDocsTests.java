package sagan.docs.support;

import org.junit.Before;
import org.junit.Test;
import saganx.AbstractIntegrationTests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ViewDocsTests extends AbstractIntegrationTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getDocsPage() throws Exception {
        mockMvc
                .perform(get("/docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Getting Started Guides")))
                .andExpect(content().string(containsString("Tutorials")))
                .andExpect(content().string(containsString("Reference Documentation")))
                .andExpect(content().string(containsString("Sagan Application")));
    }

    @Test
    public void getRefDocsPage() throws Exception {
        mockMvc
                .perform(get("/docs/reference"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Spring Security")))
                .andExpect(content().string(containsString("http://docs.spring.io/spring/docs")));
    }

}
