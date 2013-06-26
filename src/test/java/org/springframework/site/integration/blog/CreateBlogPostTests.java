package org.springframework.site.integration.blog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.blog.PostCategory;
import org.springframework.site.blog.repository.PostRepository;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
public class CreateBlogPostTests {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private PostRepository postRepository;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		postRepository.deleteAll();
	}

	@After
	public void tearDown() throws Exception {
		postRepository.deleteAll();
	}

	@Test
	public void getNewBlogPage() throws Exception {
		this.mockMvc.perform(get("/admin/blog/new"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("New Blog Post")));
	}

	@Test
	public void redirectToBlogPostAfterCreation() throws Exception {
		MockHttpServletRequestBuilder createPostRequest = post("/admin/blog");
		createPostRequest.param("title", "Post Title");
		createPostRequest.param("content", "My Content");
		createPostRequest.param("category", PostCategory.NEWS_AND_EVENTS.name());

		this.mockMvc.perform(createPostRequest)
				.andExpect(status().isFound())
				.andExpect(new ResultMatcher() {
					@Override
					public void match(MvcResult result) {
						String redirectedUrl = result.getResponse().getRedirectedUrl();
						assertTrue("Expected redirect to blog, got: " + redirectedUrl, redirectedUrl.matches("^/blog/\\d+-post-title"));
					}
				});
	}

	@Test
	public void canViewBlogPostAtAnySlugName() throws Exception {
		Post post = PostBuilder.post().build();
		postRepository.save(post);
		this.mockMvc.perform(get("/blog/" + post.getId() + "-random-slug"))
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString(post.getTitle())));
	}

	@Test
	public void createdPostValuesArePersisted() throws Exception {
		MockHttpServletRequestBuilder createPostRequest = post("/admin/blog");
		createPostRequest.param("title", "Post Title");
		createPostRequest.param("content", "My Content");
		createPostRequest.param("category", PostCategory.ENGINEERING.name());
		createPostRequest.param("broadcast", "true");

		mockMvc.perform(createPostRequest);

		Post post = postRepository.findAll().get(0);

		assertThat(post.getTitle(), is("Post Title"));
		assertThat(post.getRawContent(), is("My Content"));
		assertThat(post.getCategory(), is(PostCategory.ENGINEERING));
		assertThat(post.isBroadcast(), is(true));
	}

	@Test
	public void persistedPostValuesAreDisplayedCorrectly() throws Exception {
		Post post = PostBuilder.post().isBroadcast().build();
		postRepository.save(post);

		MvcResult response = mockMvc.perform(get("/blog/" + post.getId()))
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString(post.getTitle())))
				.andExpect(content().string(containsString(post.getCategory().getDisplayName())))
				.andExpect(content().string(containsString("Broadcast")))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.title(), containsString(post.getTitle()));
	}

}