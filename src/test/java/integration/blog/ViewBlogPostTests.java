package integration.blog;

import integration.IntegrationTestBase;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostBuilder;
import io.spring.site.domain.blog.PostCategory;
import io.spring.site.domain.blog.PostRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ViewBlogPostTests extends IntegrationTestBase {

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

        result = this.mockMvc.perform(get("/blog/" + post.getPublicSlug())).andExpect(status().isOk());
    }

    @Test
    public void getContentType() throws Exception {
        result.andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void getTitle() throws Exception {
        result.andExpect(content().string(containsString("Title")));
    }

    @Test
    public void getTwitterUrl() throws Exception {
        String twitterUrl = "https://twitter.com/intent/tweet?text=@springcentral&amp;url=http://spring.io/blog/" + post.getPublicSlug();
        result.andExpect(content().string(containsString(twitterUrl)));
    }
}
