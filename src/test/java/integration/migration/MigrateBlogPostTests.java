package integration.migration;

import integration.configuration.IntegrationTestsConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.blog.PostRepository;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IntegrationTestsConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public class MigrateBlogPostTests {
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
		author.setMemberId("johndoe");
		teamRepository.save(author);
	}

	@Test
	public void postToMigrateBlogPost() throws Exception {
		MockHttpServletRequestBuilder migrateBlogPost = post("/migration/blogpost");
		migrateBlogPost.param("title", "a post title");
		migrateBlogPost.param("content", "sample post content");
		migrateBlogPost.param("category", "ENGINEERING");
		migrateBlogPost.param("publishAt", "2000-01-01 00:00");
		migrateBlogPost.param("createdAt", "1999-01-01 00:00");
		migrateBlogPost.param("authorMemberId", author.getMemberId());

		mockMvc.perform(migrateBlogPost).andExpect(status().isOk());

		Post post = postRepository.findByTitle("a post title");

		assertThat(post.getRawContent(), is("sample post content"));
		assertThat(post.getCategory(), is(PostCategory.ENGINEERING));
		assertThat(post.getAuthor().getMemberId(), is(author.getMemberId()));

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
						.category(PostCategory.ENGINEERING)
						.build();

		postRepository.save(post);

		MockHttpServletRequestBuilder migrateBlogPost = post("/migration/blogpost");
		migrateBlogPost.param("title", "a post title");
		migrateBlogPost.param("content", "NEW post content");
		migrateBlogPost.param("category", "ENGINEERING");
		migrateBlogPost.param("publishAt", "2000-01-01 00:00");

		mockMvc.perform(migrateBlogPost).andExpect(status().isOk());

		Post updatedPost = postRepository.findByTitle("a post title");

		assertThat(updatedPost.getRawContent(), is("NEW post content"));
	}

}
