package integration.blog;

import integration.IntegrationTestBase;
import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.blog.PostRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static requestpostprocessors.SecurityRequestPostProcessors.*;

public class EditBlogPostTests extends IntegrationTestBase {

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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
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
        this.mockMvc.perform(get("/admin/blog/" + post.getAdminSlug() + "/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Edit &middot; Original Title")));
    }

    @Test
    public void redirectToPublishedPostAfterUpdate() throws Exception {
        MockHttpServletRequestBuilder editPostRequest = createEditPostRequest();

        this.mockMvc.perform(editPostRequest)
                .andExpect(status().isFound())
                .andExpect(new ResultMatcher() {
                    @Override
                    public void match(MvcResult result) {
                        String redirectedUrl = result.getResponse().getRedirectedUrl();
                        assertThat(redirectedUrl, startsWith("/blog/" + post.getPublicSlug()));
                    }
                });
    }

    @Test
    public void updatePostSavesNewValues() throws Exception {
        MockHttpServletRequestBuilder editPostRequest = createEditPostRequest();

        this.mockMvc.perform(editPostRequest);
        Post updatedPost = postRepository.findOne(post.getId());

        assertEquals("New Title", updatedPost.getTitle());
        assertEquals("New Content", updatedPost.getRawContent());
        assertEquals(PostCategory.NEWS_AND_EVENTS, updatedPost.getCategory());
        assertEquals(false, updatedPost.isDraft());
        assertThat(updatedPost.getId(), equalTo(post.getId()));
    }

    @Test
    public void updateDoesNotPersistInvalidData() throws Exception {
        MockHttpServletRequestBuilder editPostRequest = put("/admin/blog/" + post.getAdminSlug());
        editPostRequest.param("title", "");
        editPostRequest.param("content", "");
        editPostRequest.param("category", PostCategory.NEWS_AND_EVENTS.name());
        editPostRequest.param("draft", "false");

        this.mockMvc.perform(editPostRequest).andExpect(status().isOk());
        Post updatedPost = postRepository.findOne(post.getId());

        assertEquals("Original Title", updatedPost.getTitle());
        assertEquals("Original Content", updatedPost.getRawContent());
        assertEquals(PostCategory.ENGINEERING, updatedPost.getCategory());
        assertEquals(false, updatedPost.isDraft());
        assertThat(updatedPost.getId(), equalTo(post.getId()));
    }

    private MockHttpServletRequestBuilder createEditPostRequest() {
        MockHttpServletRequestBuilder editPostRequest = put("/admin/blog/" + post.getAdminSlug());
        editPostRequest.param("title", "New Title");
        editPostRequest.param("content", "New Content");
        editPostRequest.param("category", PostCategory.NEWS_AND_EVENTS.name());
        editPostRequest.param("draft", "false");
        return editPostRequest;
    }

}