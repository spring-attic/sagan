package saganx;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.support.BlogService;

import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sagan.support.SecurityRequestPostProcessors.user;

@ContextConfiguration(classes = { AuthorizationTests.Config.class })
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
        List<String> actuatorPaths = Arrays.asList(
                "/metrics", "/beans", "/autoconfig", "/env", "/mappings", "/dump", "/configprops", "/trace");

        for (String path : actuatorPaths) {
            mockMvc.perform(get(path)).andExpect(status().isUnauthorized());

            mockMvc.perform(get(path + "/")).andExpect(status().is4xxClientError());
            mockMvc.perform(get(path + ".")).andExpect(status().is4xxClientError());
            mockMvc.perform(get(path + ".json")).andExpect(status().is4xxClientError());
        }

        // endpoints.shutdown.enabled=true must be specified for /shutdown to be enabled.
        // Ensure it hasn't been.
        mockMvc.perform(post("/shutdown")).andExpect(status().is4xxClientError());
    }

    @Test
    public void editForAuthenticatedUsers() throws Exception {
        Post post = PostBuilder.post().id(1L).title("post title").build();
        when(blogService.getPublishedPost(anyString())).thenReturn(post);

        MockHttpServletResponse response = mockMvc.perform(get("/blog/2012/02/01/title").with(user(123L).roles("USER")))
            .andReturn().getResponse();
        Document html = Jsoup.parse(response.getContentAsString());
        Element edit = html.select("#edit").first();

        assertThat("Edit should be found", edit, is(notNullValue()));
        assertThat(edit.attr("href"), is("/admin/blog/1-title/edit"));
    }

    @Test
    public void noEditForUnAuthenticatedUsers() throws Exception {
        Post post = PostBuilder.post().id(1L).title("post title").build();
        when(blogService.getPublishedPost(anyString())).thenReturn(post);

        MockHttpServletResponse response = mockMvc.perform(get("/blog/2012/02/01/title"))
            .andReturn().getResponse();

        Document html = Jsoup.parse(response.getContentAsString());
        Element edit = html.select("#edit").first();

        assertThat("Edit should not be found", edit, is(nullValue()));
    }

    @Configuration
    static class Config {
        @Primary
        @Bean
        public BlogService mockBlogService() {
            return mock(BlogService.class);
        }
    }
}
