package integration.blog;

import integration.IntegrationTestBase;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostBuilder;
import io.spring.site.domain.blog.PostCategory;
import io.spring.site.domain.blog.PostRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ViewBlogPostTests extends IntegrationTestBase {

    @Autowired
    private PostRepository postRepository;

    private Post post;

    @Before
    public void setup() throws ParseException {
        post = PostBuilder.post()
                .publishAt("2013-04-01 11:00")
                .title("Title")
                .rawContent("Content")
                .category(PostCategory.ENGINEERING).build();
        postRepository.save(post);
    }

    @Test
    public void getBlogPage() throws Exception {
        this.mockMvc.perform(get("/blog/" + post.getPublicSlug()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Title")));
    }
}
