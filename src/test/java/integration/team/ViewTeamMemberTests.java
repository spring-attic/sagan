package integration.team;

import integration.configuration.SiteOfflineConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.blog.PostRepository;
import org.springframework.site.team.MemberProfile;
import org.springframework.site.team.TeamRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SiteOfflineConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public class ViewTeamMemberTests {
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private PostRepository postRepository;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void getTeamMemberPage() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setName("First Last");
		profile.setGithubUsername("someguy");
		profile.setMemberId("someguy");

		teamRepository.save(profile);

		this.mockMvc.perform(get("/about/team/someguy"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("First Last")))
				.andExpect(content().string(containsString("someguy")));
	}

	@Test
	public void getTeamMemberPageShowsPosts() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setName("First Last");
		profile.setGithubUsername("someguy");
		profile.setMemberId("someguy");

		teamRepository.save(profile);

		Post post = PostBuilder.post().author(profile).title("My Post").build();
		postRepository.save(post);

		this.mockMvc.perform(get("/about/team/someguy"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("My Post")));
	}

	@Test
	public void getNonExistentTeamMemberPage() throws Exception {
		this.mockMvc.perform(get("/about/team/someguy"))
				.andExpect(status().isNotFound());
	}
}
