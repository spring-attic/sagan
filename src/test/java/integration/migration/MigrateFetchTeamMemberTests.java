package integration.migration;

import integration.IntegrationTestBase;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.TeamRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MigrateFetchTeamMemberTests extends IntegrationTestBase {
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
		MemberProfile john = new MemberProfile();
		john.setName("John Doe");
		john.setUsername("jdoe");

		teamRepository.save(john);

		MemberProfile guy = new MemberProfile();
		guy.setName("Some Guy");
		guy.setUsername("someguy");

		teamRepository.save(guy);

		MockHttpServletRequestBuilder migrateProfile = get("/migration/team_members");

		mockMvc.perform(migrateProfile).andExpect(status().isOk())
				.andExpect(content().string(containsString("\"John Doe\":\"jdoe\"")))
				.andExpect(content().string(containsString("\"Some Guy\":\"someguy\"")));

	}
}
