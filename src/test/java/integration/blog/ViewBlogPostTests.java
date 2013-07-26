package integration.blog;

import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.blog.PostRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ViewBlogPostTests extends IntegrationTestBase {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private PostRepository postRepository;

	private MockMvc mockMvc;
	private Post post;

	@Before
	public void setup() throws ParseException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
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
