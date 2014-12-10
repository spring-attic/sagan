package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.blog.PostFormat;
import saganx.AbstractIntegrationTests;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sagan.support.SecurityRequestPostProcessors.*;

public class EditBlogPostTests extends AbstractIntegrationTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    private Post post;
    private String originalTitle = "Original Title";
    private String originalContent = "Original Content";
    private PostCategory originalCategory = PostCategory.ENGINEERING;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(springSecurityFilterChain)
                .defaultRequest(get("/").with(csrf()).with(user(123L).roles("USER"))).build();
        post = PostBuilder.post()
                .title(originalTitle)
                .rawContent(originalContent)
                .category(originalCategory).build();
        postRepository.save(post);
    }

    @Test
    public void getEditBlogPage() throws Exception {
        mockMvc.perform(get("/admin/blog/" + post.getAdminSlug() + "/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Edit &middot; Original Title")));
    }

    @Test
    public void displayEditPostAfterUpdate() throws Exception {
        MockHttpServletRequestBuilder editPostRequest = createEditPostRequest();

        mockMvc.perform(editPostRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void updatePostSavesNewValues() throws Exception {
        MockHttpServletRequestBuilder editPostRequest = createEditPostRequest();

        String originalPublicSlug = post.getPublicSlug();
        mockMvc.perform(editPostRequest);
        Post updatedPost = postRepository.findOne(post.getId());

        assertEquals("New Title", updatedPost.getTitle());
        assertEquals("New Content", updatedPost.getRawContent());
        assertEquals(PostCategory.NEWS_AND_EVENTS, updatedPost.getCategory());
        assertEquals(PostFormat.MARKDOWN, updatedPost.getFormat());
        assertEquals(false, updatedPost.isDraft());
        assertThat(updatedPost.getId(), equalTo(post.getId()));

        mockMvc.perform(get("/blog/{slug}", originalPublicSlug))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/" + updatedPost.getPublicSlug()));
    }

    @Test
    public void updateDoesNotPersistInvalidData() throws Exception {
        MockHttpServletRequestBuilder editPostRequest = put("/admin/blog/" + post.getAdminSlug() + "/edit");
        editPostRequest.param("title", "");
        editPostRequest.param("content", "");
        editPostRequest.param("category", PostCategory.NEWS_AND_EVENTS.name());
        editPostRequest.param("draft", "false");

        mockMvc.perform(editPostRequest).andExpect(status().isOk());
        Post updatedPost = postRepository.findOne(post.getId());

        assertEquals("Original Title", updatedPost.getTitle());
        assertEquals("Original Content", updatedPost.getRawContent());
        assertEquals(PostCategory.ENGINEERING, updatedPost.getCategory());
        assertEquals(false, updatedPost.isDraft());
        assertThat(updatedPost.getId(), equalTo(post.getId()));
    }

    private MockHttpServletRequestBuilder createEditPostRequest() {
        MockHttpServletRequestBuilder editPostRequest = put("/admin/blog/" + post.getAdminSlug() + "/edit");
        editPostRequest.param("title", "New Title");
        editPostRequest.param("content", "New Content");
        editPostRequest.param("category", PostCategory.NEWS_AND_EVENTS.name());
        editPostRequest.param("format", PostFormat.MARKDOWN.name());
        editPostRequest.param("draft", "false");
        return editPostRequest;
    }

}
