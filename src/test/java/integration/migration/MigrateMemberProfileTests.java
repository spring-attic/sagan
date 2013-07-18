package integration.migration;

import integration.configuration.IntegrationTestsConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
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

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IntegrationTestsConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public class MigrateMemberProfileTests {
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
		migrateProfile.param("memberId", "migrate_someguy");
		migrateProfile.param("name", "Some_ Guy_");
		migrateProfile.param("githubUsername", "migrate_someguy");
		migrateProfile.param("gravatarEmail", "someguy@example.com");

		mockMvc.perform(migrateProfile).andExpect(status().isOk());

		MemberProfile profile = teamRepository.findByMemberId("migrate_someguy");

		assertEquals("migrate_someguy", profile.getMemberId());
		assertEquals("Some_ Guy_", profile.getName());
		assertEquals("migrate_someguy", profile.getGithubUsername());
		assertEquals("someguy@example.com", profile.getGravatarEmail());
	}

	@Test
	public void postToMigrateMemberDoesNotDuplicateProfiles() throws Exception {
		MemberProfile memberProfile = new MemberProfile();
		memberProfile.setMemberId("migrate_someguy");
		teamRepository.save(memberProfile);

		MockHttpServletRequestBuilder migrateProfile = post("/migration/profile");
		migrateProfile.param("memberId", "migrate_someguy");
		mockMvc.perform(migrateProfile).andExpect(status().isOk());

		MemberProfile profile = teamRepository.findByMemberId("migrate_someguy");

		assertThat(profile, not(nullValue()));
	}

	@Test
	public void postToMigrateMemberDoesNotUpdateExistingProfiles() throws Exception {
		MemberProfile memberProfile = new MemberProfile();
		memberProfile.setMemberId("migrate_someguy");
		memberProfile.setName("First Guy");
		teamRepository.save(memberProfile);

		MockHttpServletRequestBuilder migrateProfile = post("/migration/profile");
		migrateProfile.param("memberId", "migrate_someguy");
		migrateProfile.param("name", "Second Guy");
		mockMvc.perform(migrateProfile).andExpect(status().isOk());

		MemberProfile profile = teamRepository.findByMemberId("migrate_someguy");

		assertEquals("First Guy", profile.getName());
	}
}
