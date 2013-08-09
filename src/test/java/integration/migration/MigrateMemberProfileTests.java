package integration.migration;

import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MigrateMemberProfileTests extends IntegrationTestBase {
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TeamRepository teamRepository;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void postToMigrateMemberProfile() throws Exception {
		MockHttpServletRequestBuilder migrateProfile = post("/migration/profile");
		migrateProfile.param("username", "migrate_someguy");
		migrateProfile.param("name", "Some_ Guy_");
		migrateProfile.param("githubUsername", "migrate_someguy");
		migrateProfile.param("gravatarEmail", "someguy@example.com");

		mockMvc.perform(migrateProfile).andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/team/migrate_someguy")));

		MemberProfile profile = teamRepository.findByUsername("migrate_someguy");

		assertThat(profile.getUsername(), is("migrate_someguy"));
		assertThat(profile.getName(), is("Some_ Guy_"));
		assertThat(profile.getGithubUsername(), is("migrate_someguy"));
		assertThat(profile.getGravatarEmail(), is("someguy@example.com"));
		assertThat(profile.isHidden(), is(true));
	}

	@Test
	public void postToMigrateMemberDoesNotDuplicateProfiles() throws Exception {
		MemberProfile memberProfile = new MemberProfile();
		memberProfile.setUsername("migrate_someguy");
		teamRepository.save(memberProfile);

		MockHttpServletRequestBuilder migrateProfile = post("/migration/profile");
		migrateProfile.param("username", "migrate_someguy");
		mockMvc.perform(migrateProfile).andExpect(status().isOk());

		MemberProfile profile = teamRepository.findByUsername("migrate_someguy");

		assertThat(profile, not(nullValue()));
	}

	@Test
	public void postToMigrateMemberDoesNotUpdateExistingProfiles() throws Exception {
		MemberProfile memberProfile = new MemberProfile();
		memberProfile.setUsername("migrate_someguy");
		memberProfile.setName("First Guy");
		teamRepository.save(memberProfile);

		MockHttpServletRequestBuilder migrateProfile = post("/migration/profile");
		migrateProfile.param("username", "migrate_someguy");
		migrateProfile.param("name", "Second Guy");
		mockMvc.perform(migrateProfile).andExpect(status().isOk());

		MemberProfile profile = teamRepository.findByUsername("migrate_someguy");

		assertEquals("First Guy", profile.getName());
	}
}
