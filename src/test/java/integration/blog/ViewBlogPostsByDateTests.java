package integration.blog;

import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.blog.PostRepository;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ViewBlogPostsByDateTests extends IntegrationTestBase {
	@Autowired
	private PostRepository postRepository;

	private Post post1, post2, post3;

	@Before
	public void setup() throws ParseException {
		post1 = PostBuilder.post()
				.publishAt("2013-04-01 11:00")
				.title("Title 1")
				.rawContent("Content")
				.category(PostCategory.ENGINEERING).build();
		postRepository.save(post1);

		post2 = PostBuilder.post()
				.publishAt("2013-04-02 11:00")
				.title("Title 2")
				.rawContent("Content")
				.category(PostCategory.ENGINEERING).build();
		postRepository.save(post2);

		post3 = PostBuilder.post()
				.publishAt("2013-03-02 11:00")
				.title("Title 3")
				.rawContent("Content")
				.category(PostCategory.ENGINEERING).build();
		postRepository.save(post3);

		Post oldPost = PostBuilder.post()
				.publishAt("2012-04-01 11:00")
				.title("Old Post")
				.rawContent("Content")
				.category(PostCategory.ENGINEERING).build();
		postRepository.save(oldPost);

	}

	@Test
	public void getDateReturnsPostsForDate() throws Exception {
		this.mockMvc.perform(get("/blog/2013/04/01"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("Title 1")))
				.andExpect(content().string(not(containsString("Title 2"))))
				.andExpect(content().string(not(containsString("Title 3"))))
				.andExpect(content().string(not(containsString("Old Post"))));
	}

	@Test
	public void getMonthReturnsPostsForMonth() throws Exception {
		this.mockMvc.perform(get("/blog/2013/04"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("Title 1")))
				.andExpect(content().string(containsString("Title 2")))
				.andExpect(content().string(not(containsString("Title 3"))))
				.andExpect(content().string(not(containsString("Old Post"))));
	}

	@Test
	public void getMonthReturnsPostsForYear() throws Exception {
		this.mockMvc.perform(get("/blog/2013"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("Title 1")))
				.andExpect(content().string(containsString("Title 2")))
				.andExpect(content().string(containsString("Title 3")))
				.andExpect(content().string(not(containsString("Old Post"))));
	}
}
