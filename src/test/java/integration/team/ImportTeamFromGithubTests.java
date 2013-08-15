package integration.team;

import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.site.test.FixtureLoader;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ImportTeamFromGithubTests extends IntegrationTestBase {

	@Autowired
	private TeamRepository teamRepository;

	@Before
	public void setUp() throws Exception {
		String membersJson = FixtureLoader.load("/fixtures/github/ghTeamInfo.json");
		stubRestClient.putResponse("/orgs/springframework-meta/members", membersJson);
	}

	@Test
	public void importAddsNewTeamMembers() throws Exception {
		mockMvc.perform(post("/admin/team/github_import")).andExpect(new ResultMatcher() {
			@Override
			public void match(MvcResult result) {
				String redirectedUrl = result.getResponse().getRedirectedUrl();
				assertThat(redirectedUrl, equalTo("/admin/team"));
			}
		});

		MemberProfile profile = teamRepository.findByGithubId(123L);
		assertThat(profile, not(nullValue()));
		assertThat(profile.getGithubUsername(), equalTo("jdoe"));
		assertThat(profile.getUsername(), equalTo("jdoe"));
		assertThat(profile.isHidden(), equalTo(false));

		MemberProfile profile2 = teamRepository.findByGithubId(987L);
		assertThat(profile2, not(nullValue()));
		assertThat(profile2.getGithubUsername(), equalTo("asmith"));
		assertThat(profile2.isHidden(), equalTo(false));
	}

	@Test
	public void importUpdatesExistingTeamMembers() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setGithubId(123L);
		profile.setGithubUsername("oldusername");
		profile.setUsername("oldusername");
		teamRepository.save(profile);

		mockMvc.perform(post("/admin/team/github_import")).andExpect(new ResultMatcher() {
			@Override
			public void match(MvcResult result) {
				String redirectedUrl = result.getResponse().getRedirectedUrl();
				assertThat(redirectedUrl, equalTo("/admin/team"));
			}
		});

		MemberProfile updatedProfile = teamRepository.findByGithubId(123L);
		assertThat(updatedProfile, not(nullValue()));
		assertThat(updatedProfile.getGithubUsername(), equalTo("jdoe"));
		assertThat(updatedProfile.getUsername(), equalTo("oldusername"));
		assertThat(updatedProfile.isHidden(), equalTo(false));
	}

	@Test
	public void importHidesExistingMembersNoLongerOnTheTeam() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setGithubId(456L);
		profile.setGithubUsername("quitter");
		profile.setUsername("quitter");
		profile.setHidden(false);
		teamRepository.save(profile);

		mockMvc.perform(post("/admin/team/github_import")).andExpect(new ResultMatcher() {
			@Override
			public void match(MvcResult result) {
				String redirectedUrl = result.getResponse().getRedirectedUrl();
				assertThat(redirectedUrl, equalTo("/admin/team"));
			}
		});

		MemberProfile updatedProfile = teamRepository.findByGithubId(456L);
		assertThat(updatedProfile, not(nullValue()));
		assertThat(updatedProfile.getGithubUsername(), equalTo("quitter"));
		assertThat(updatedProfile.isHidden(), equalTo(true));
	}

}
