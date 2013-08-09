package integration.team;

import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostRepository;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ViewTeamMemberTests extends IntegrationTestBase {
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
		profile.setUsername("someguy");

		teamRepository.save(profile);

		this.mockMvc.perform(get("/team/someguy"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("First Last")))
				.andExpect(content().string(containsString("someguy")));
	}

	@Test
	public void getTeamMemberPage_404sWhenNoMemberFound() throws Exception {
		this.mockMvc.perform(get("/team/not-a-user")).andExpect(status().isNotFound());
	}

	@Test
	public void getTeamMemberPageShowsPosts() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setName("First Last");
		profile.setGithubUsername("someguy");
		profile.setUsername("someguy");

		teamRepository.save(profile);

		Post post = PostBuilder.post().author(profile).title("My Post").build();
		postRepository.save(post);

		this.mockMvc.perform(get("/team/someguy"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("My Post")));
	}

	@Test
	public void getNonExistentTeamMemberPage() throws Exception {
		this.mockMvc.perform(get("/team/someguy"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getTeamMemberPageForNameWithDashes() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setName("First Last");
		profile.setGithubUsername("some-guy");
		profile.setUsername("some-guy");

		teamRepository.save(profile);

		this.mockMvc.perform(get("/team/some-guy"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("First Last")))
				.andExpect(content().string(containsString("some-guy")));
	}

}
