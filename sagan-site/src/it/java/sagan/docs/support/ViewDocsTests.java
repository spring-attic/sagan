package sagan.docs.support;

import saganx.AbstractIntegrationTests;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(content().string(containsString("Spring Security")))
                .andExpect(content().string(containsString("http://docs.spring.io/spring/docs")));
    }

    @Test
    public void doesNotContainAggregatorProjects() throws Exception {
        mockMvc
                .perform(get("/docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(CoreMatchers.containsString(">Spring Data JPA<")))
                .andExpect(content().string(not(CoreMatchers.containsString(">Spring Data<"))));
    }

}
