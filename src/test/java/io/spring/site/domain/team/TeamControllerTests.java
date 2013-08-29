package io.spring.site.domain.team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;

import io.spring.site.domain.blog.BlogService;
import io.spring.site.domain.services.DateService;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.TeamLocation;
import io.spring.site.domain.team.TeamService;
import io.spring.site.web.blog.PostViewFactory;
import io.spring.site.web.team.TeamController;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TeamControllerTests {


	@Mock
	BlogService blogService;

	@Mock
	private TeamService teamService;

	private ExtendedModelMap model = new ExtendedModelMap();

	TeamController teamController;

	@Before
	public void setUp() throws Exception {
		this.teamController = new TeamController(this.teamService, this.blogService,
				new PostViewFactory(new DateService()));
		List<MemberProfile> members = new ArrayList<>();

		members.add(MemberProfileBuilder.profile()
				.name("Norman")
				.geoLocation(10, 5)
				.username("normy")
				.id(123L)
				.build());
		members.add(MemberProfileBuilder.profile()
				.name("Patrick")
				.geoLocation(-5, 15)
				.username("pat")
				.id(321L)
				.build());

		given(this.teamService.fetchActiveMembers()).willReturn(members);
	}

	@Test
	public void includeTeamLocationsInModel() throws Exception {
		this.teamController.showTeam(this.model);
		@SuppressWarnings("unchecked")
		List<TeamLocation> teamLocations = (List<TeamLocation>) this.model
				.get("teamLocations");

		TeamLocation norman = teamLocations.get(0);
		assertThat(norman.getName(), equalTo("Norman"));
		assertThat(norman.getLatitude(), equalTo(10f));
		assertThat(norman.getLongitude(), equalTo(5f));
		assertThat(norman.getMemberId(), equalTo(123L));

		TeamLocation patrick = teamLocations.get(1);
		assertThat(patrick.getName(), equalTo("Patrick"));
		assertThat(patrick.getLatitude(), equalTo(-5f));
		assertThat(patrick.getLongitude(), equalTo(15f));
		assertThat(patrick.getMemberId(), equalTo(321L));
	}
}
