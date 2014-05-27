package sagan.team.support;

import sagan.blog.support.BlogService;
import sagan.support.DateFactory;
import sagan.team.MemberProfile;
import sagan.team.MemberProfileBuilder;
import sagan.team.TeamLocation;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.ui.ExtendedModelMap;

/**
 * Unit tests for {@link TeamController}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamControllerTests {

    @Mock
    private BlogService blogService;

    @Mock
    private TeamService teamService;

    private ExtendedModelMap model = new ExtendedModelMap();

    private TeamController teamController;
    private DateFactory dateFactory = new DateFactory();

    @Before
    public void setUp() throws Exception {
        teamController = new TeamController(teamService, blogService, dateFactory);
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

        BDDMockito.given(teamService.fetchActiveMembers()).willReturn(members);
    }

    @Test
    public void includeTeamLocationsInModel() throws Exception {
        teamController.showTeam(model);
        @SuppressWarnings("unchecked")
        List<TeamLocation> teamLocations = (List<TeamLocation>) model.get("teamLocations");

        TeamLocation norman = teamLocations.get(0);
        MatcherAssert.assertThat(norman.getName(), Matchers.equalTo("Norman"));
        MatcherAssert.assertThat(norman.getLatitude(), Matchers.equalTo(10f));
        MatcherAssert.assertThat(norman.getLongitude(), Matchers.equalTo(5f));
        MatcherAssert.assertThat(norman.getMemberId(), Matchers.equalTo(123L));

        TeamLocation patrick = teamLocations.get(1);
        MatcherAssert.assertThat(patrick.getName(), Matchers.equalTo("Patrick"));
        MatcherAssert.assertThat(patrick.getLatitude(), Matchers.equalTo(-5f));
        MatcherAssert.assertThat(patrick.getLongitude(), Matchers.equalTo(15f));
        MatcherAssert.assertThat(patrick.getMemberId(), Matchers.equalTo(321L));
    }
}
