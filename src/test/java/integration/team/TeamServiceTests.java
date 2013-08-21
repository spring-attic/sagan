package integration.team;

import integration.IntegrationTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TeamServiceTests extends IntegrationTestBase{

	@Autowired
	private TeamService teamService;

	@Test
	public void newlyCreatedTeamMembersAreHiddenByDefault() throws Exception {
		MemberProfile jdoe = teamService.createOrUpdateMemberProfile(123L, "jdoe", "http://avatarurl.com", "John Doe");
		assertThat(jdoe.isHidden(), is(true));
	}
}
