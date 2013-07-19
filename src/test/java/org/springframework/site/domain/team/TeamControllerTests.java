package org.springframework.site.domain.team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.web.blog.PostViewFactory;
import org.springframework.site.web.team.TeamController;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TeamControllerTests {

	TeamController teamController;

	@Mock
	TeamRepository teamRepository;

	@Mock
	BlogService blogService;

	private ExtendedModelMap model = new ExtendedModelMap();

	@Before
	public void setUp() throws Exception {
		this.teamController = new TeamController(this.teamRepository, this.blogService,
				new PostViewFactory(new DateService()));
		List<MemberProfile> members = new ArrayList<>();

		members.add(MemberProfileBuilder.profile().name("Norman").geoLocation(10, 5).memberId("normy").build());
		members.add(MemberProfileBuilder.profile().name("Patrick").geoLocation(-5, 15).memberId("pat").build());

		given(this.teamRepository.findByHidden(false)).willReturn(members);
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
		assertThat(norman.getMemberId(), equalTo("normy"));

		TeamLocation patrick = teamLocations.get(1);
		assertThat(patrick.getName(), equalTo("Patrick"));
		assertThat(patrick.getLatitude(), equalTo(-5f));
		assertThat(patrick.getLongitude(), equalTo(15f));
		assertThat(patrick.getMemberId(), equalTo("pat"));
	}
}
