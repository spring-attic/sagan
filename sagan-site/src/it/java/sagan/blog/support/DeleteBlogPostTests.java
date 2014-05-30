package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import saganx.AbstractIntegrationTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteBlogPostTests extends AbstractIntegrationTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    private Post post;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        postRepository.deleteAll();
        post = PostBuilder.post().build();
        postRepository.save(post);
    }

    @After
    public void tearDown() throws Exception {
        postRepository.deleteAll();
    }

    @Test
    public void redirectToIndexAfterDelete() throws Exception {
        MockHttpServletRequestBuilder editPostRequest = createDeletePostRequest();

        mockMvc.perform(editPostRequest)
                .andExpect(status().isFound())
                .andExpect(result -> {
                    String redirectedUrl = result.getResponse().getRedirectedUrl();
                    assertThat(redirectedUrl, startsWith("/admin/blog"));
                });
    }

    @Test
    public void deleteThePost() throws Exception {
        MockHttpServletRequestBuilder editPostRequest = createDeletePostRequest();

        mockMvc.perform(editPostRequest);
        assertThat(postRepository.findOne(post.getId()), is(nullValue()));
    }

    private MockHttpServletRequestBuilder createDeletePostRequest() {
        MockHttpServletRequestBuilder editPostRequest = delete("/admin/blog/" + post.getAdminSlug());
        return editPostRequest;
    }

}
