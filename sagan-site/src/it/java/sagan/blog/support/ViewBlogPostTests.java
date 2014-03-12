package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import saganx.AbstractIntegrationTests;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ViewBlogPostTests extends AbstractIntegrationTests {

    @Autowired
    private PostRepository postRepository;

    private Post post;
    private ResultActions result;

    @Before
    public void setup() throws Exception {
        post = PostBuilder.post()
                .publishAt("2013-04-01 11:00")
                .title("Title")
                .rawContent("Content")
                .category(PostCategory.ENGINEERING).build();
        postRepository.save(post);

        result = mockMvc.perform(get("/blog/" + post.getPublicSlug())).andExpect(status().isOk());
    }

    @Test
    public void getContentType() throws Exception {
        result.andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void getTitle() throws Exception {
        result.andExpect(content().string(containsString("Title")));
    }
}
