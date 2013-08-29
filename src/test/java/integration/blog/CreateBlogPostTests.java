package integration.blog;

import integration.IntegrationTestBase;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostCategory;
import io.spring.site.domain.blog.PostRepository;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.TeamRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static requestpostprocessors.SecurityRequestPostProcessors.*;

public class CreateBlogPostTests extends IntegrationTestBase {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private TeamRepository teamRepository;

	private Principal principal;

	@Before
	public void setup() {
		MemberProfile profile = new MemberProfile();
		profile.setUsername("author");
		profile.setName("Mr Author");
		profile = teamRepository.save(profile);

		final String profileId = profile.getId().toString();

		principal = new Principal() {
			@Override
			public String getName() {
				return profileId;
			}
		};
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilters(springSecurityFilterChain)
				.defaultRequest(get("/").with(csrf()).with(user(principal.getName()).roles("USER"))).build();
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
				.andExpect(content().string(containsString("Add New Post")));
	}

	@Test
	public void redirectToPublishedPostAfterCreation() throws Exception {
		MockHttpServletRequestBuilder createPostRequest = getCreatePostRequest();
		createPostRequest.param("title", "Post Title");
		createPostRequest.param("content", "My Content");
		createPostRequest.param("category", PostCategory.NEWS_AND_EVENTS.name());
        createPostRequest.param("draft", "false");
		createPostRequest.param("publishAt", "2013-07-01 13:15");

		this.mockMvc.perform(createPostRequest)
				.andExpect(status().isFound())
				.andExpect(new ResultMatcher() {
					@Override
					public void match(MvcResult result) {
						String redirectedUrl = result.getResponse().getRedirectedUrl();
						assertTrue("Expected redirect to /blog/2013/07/01/post-title, got: " + redirectedUrl, redirectedUrl.matches("^/blog/2013/07/01/post-title"));
					}
				});
	}

	@Test
	public void createdPostValuesArePersisted() throws Exception {
		MockHttpServletRequestBuilder createPostRequest = getCreatePostRequest();
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

	private MockHttpServletRequestBuilder getCreatePostRequest() {
		return post("/admin/blog").principal(principal);
	}

	@Test
	public void invalidPostsShowsErrors() throws Exception {
		MockHttpServletRequestBuilder createPostRequest = getCreatePostRequest();
		mockMvc.perform(createPostRequest)
				.andExpect(status().isOk());
	}

}