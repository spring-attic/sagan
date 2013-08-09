package integration.migration;

import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.blog.PostRepository;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;

import static matchers.RegexMatcher.matches;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MigrateBlogPostTests extends IntegrationTestBase {
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private TeamRepository teamRepository;

	private MockMvc mockMvc;
	private MemberProfile author;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		author = new MemberProfile();
		author.setUsername("johndoe");
		teamRepository.save(author);
	}

	@Test
	public void postToMigrateBlogPost() throws Exception {
		MockHttpServletRequestBuilder migrateBlogPost = post("/migration/blogpost/" + author.getUsername());
		migrateBlogPost.param("title", "a post title");
		migrateBlogPost.param("content", "sample post content");
		migrateBlogPost.param("category", "ENGINEERING");
		migrateBlogPost.param("publishAt", "2000-01-01 00:00");
		migrateBlogPost.param("createdAt", "1999-01-01 00:00");

		mockMvc.perform(migrateBlogPost).andExpect(status().isCreated())
				.andExpect(header().string("Location", matches("/blog/2000/01/01/a-post-title")));

		Post post = postRepository.findByTitle("a post title");

		assertThat(post.getRawContent(), is("sample post content"));
		assertThat(post.getCategory(), is(PostCategory.ENGINEERING));
		assertThat(post.getAuthor().getUsername(), is(author.getUsername()));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		String publishAt = dateFormat.format(post.getPublishAt());
		assertThat(publishAt, is("2000-01-01 00:00"));

		String createdAt = dateFormat.format(post.getCreatedAt());
		assertThat(createdAt, is("1999-01-01 00:00"));
	}

	@Test
	public void updatesBlogPostIfItExists() throws Exception {
		Post post = PostBuilder.post()
						.author(author)
						.title("a post title")
						.rawContent("original post content")
						.publishAt("2000-01-01 00:00")
						.createdAt("1999-11-21 10:00")
						.category(PostCategory.ENGINEERING)
						.build();

		postRepository.save(post);

		MockHttpServletRequestBuilder migrateBlogPost = post("/migration/blogpost/" + author.getUsername());
		migrateBlogPost.param("title", "a post title");
		migrateBlogPost.param("content", "NEW post content");
		migrateBlogPost.param("category", "ENGINEERING");
		migrateBlogPost.param("publishAt", "2000-01-01 00:00");
		migrateBlogPost.param("createdAt", "1999-11-21 10:00");

		mockMvc.perform(migrateBlogPost).andExpect(status().isOk())
				.andExpect(header().string("Location", matches("/blog/" + post.getPublicSlug())));

		Post updatedPost = postRepository.findByTitle("a post title");

		assertThat(updatedPost.getRawContent(), is("NEW post content"));
	}

}
