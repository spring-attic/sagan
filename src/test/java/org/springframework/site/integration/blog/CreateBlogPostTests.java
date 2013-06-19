package org.springframework.site.integration.blog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.test.configuration.OfflineApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = OfflineApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
public class CreateBlogPostTests {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void getNewBlogPage() throws Exception {
		this.mockMvc.perform(get("/admin/blog/new"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("New Blog Post")));
	}

	@Test
	public void createNewBlogPost() throws Exception {
		MockHttpServletRequestBuilder createPostRequest = post("/admin/blog");
		createPostRequest.param("title", "Post Title");
		createPostRequest.param("content", "My Content");

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
	public void viewBlogPost() throws Exception {
		final AtomicReference<String> postId = new AtomicReference<String>();
		MockHttpServletRequestBuilder createPostRequest = post("/admin/blog");
		createPostRequest.param("title", "Post Title");
		createPostRequest.param("content", "My Content");
		this.mockMvc.perform(createPostRequest).andDo(new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				String redirectedUrl = result.getResponse().getRedirectedUrl();
				postId.set(redirectedUrl.substring(redirectedUrl.lastIndexOf("/") + 1));
			}
		});

		this.mockMvc.perform(get("/blog/" + postId.get() + "-sfgsgdf"))
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("<h1>Post Title</h1>")))
				.andExpect(content().string(containsString("Post Title</title>")));
	}
}