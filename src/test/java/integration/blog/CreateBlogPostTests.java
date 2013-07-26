package integration.blog;

import integration.IntegrationTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.blog.PostRepository;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
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
		profile.setMemberId("author");
		profile.setName("Mr Author");
		teamRepository.save(profile);

		principal = new Principal() {
			@Override
			public String getName() {
				return "author";
			}
		};
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