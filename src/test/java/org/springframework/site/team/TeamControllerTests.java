package org.springframework.site.team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.web.PostViewFactory;
import org.springframework.site.services.DateService;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

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
		teamController = new TeamController(teamRepository, blogService, new PostViewFactory(new DateService()));
		List<MemberProfile> all = new ArrayList<MemberProfile>();

		all.add(MemberProfileBuilder.profile().name("Norman").geoLocation(10, 5).build());
		all.add(MemberProfileBuilder.profile().name("Patrick").geoLocation(-5, 15).build());

		when(teamRepository.findAll()).thenReturn(all);
	}

	@Test
	public void includeTeamLocationsInModel() throws Exception {
		teamController.showTeam(model);
		List<TeamLocation> teamLocations = (List<TeamLocation>) model.get("teamLocations");

		TeamLocation norman = teamLocations.get(0);
		assertThat(norman.getName(), equalTo("Norman"));
		assertThat(norman.getLatitude(), equalTo(10f));
		assertThat(norman.getLongitude(), equalTo(5f));

		TeamLocation patrick = teamLocations.get(1);
		assertThat(patrick.getName(), equalTo("Patrick"));
		assertThat(patrick.getLatitude(), equalTo(-5f));
		assertThat(patrick.getLongitude(), equalTo(15f));
	}
}
