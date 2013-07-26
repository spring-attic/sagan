package integration.blog;

import integration.IntegrationTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteBlogPostTests extends IntegrationTestBase {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private PostRepository postRepository;

	private Post post;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		postRepository.deleteAll();
		post = PostBuilder.post().build();
		postRepository.save(post);
	}

	@After
	public void tearDown() throws Exception {
		postRepository.deleteAll();
	}

	@Test
	public void redirectToIndexAfterDelete() throws Exception {
		MockHttpServletRequestBuilder editPostRequest = createDeletePostRequest();

		this.mockMvc.perform(editPostRequest)
				.andExpect(status().isFound())
				.andExpect(new ResultMatcher() {
					@Override
					public void match(MvcResult result) {
						String redirectedUrl = result.getResponse().getRedirectedUrl();
						assertThat(redirectedUrl, startsWith("/admin/blog"));
					}
				});
	}

	@Test
	public void deleteThePost() throws Exception {
		MockHttpServletRequestBuilder editPostRequest = createDeletePostRequest();

		this.mockMvc.perform(editPostRequest);
		assertThat(postRepository.findOne(post.getId()), is(nullValue()));
	}

	private MockHttpServletRequestBuilder createDeletePostRequest() {
		MockHttpServletRequestBuilder editPostRequest = delete("/admin/blog/" + post.getAdminSlug());
		return editPostRequest;
	}

}