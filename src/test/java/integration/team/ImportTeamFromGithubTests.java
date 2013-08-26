package integration.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamImporter;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.site.test.FixtureLoader;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.web.client.RestOperations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ImportTeamFromGithubTests extends IntegrationTestBase {

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private TeamImporter teamImporter;

	private GitHub gitHub = mock(GitHub.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() throws Exception {
		RestOperations restOperations = mock(RestOperations.class);
		given(gitHub.restOperations()).willReturn(restOperations);

		String membersJson = FixtureLoader.load("/fixtures/github/ghTeamInfo.json");
		GitHubUser[] gitHubUsers = mapper.readValue(membersJson, GitHubUser[].class);
		ResponseEntity<GitHubUser[]> responseEntity = new ResponseEntity<>(gitHubUsers, HttpStatus.OK);

		given(restOperations.getForEntity("https://api.github.com/teams/{teamId}/members", GitHubUser[].class, "482984")).willReturn(responseEntity);

		stubRestClient.putResponse("/users/jdoe", FixtureLoader.load("/fixtures/github/ghUserProfile-jdoe.json"));
		stubRestClient.putResponse("/users/asmith", FixtureLoader.load("/fixtures/github/ghUserProfile-asmith.json"));
	}

	@Test
	public void importAddsNewTeamMembersAndSetsThemToBeHidden() throws Exception {
		teamImporter.importTeamMembers(gitHub);

		MemberProfile john = teamRepository.findByGithubId(123L);
		assertThat(john, not(nullValue()));
		assertThat(john.getGithubUsername(), equalTo("jdoe"));
		assertThat(john.getUsername(), equalTo("jdoe"));
		assertThat(john.getName(), equalTo("John Doe"));
		assertThat(john.isHidden(), equalTo(true));

		MemberProfile adam = teamRepository.findByGithubId(987L);
		assertThat(adam, not(nullValue()));
		assertThat(adam.getGithubUsername(), equalTo("asmith"));
		assertThat(adam.getName(), equalTo("Adam Smith"));
		assertThat(adam.isHidden(), equalTo(true));
	}

	@Test
	public void importUpdatesExistingTeamMembersGithubUsername() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setGithubId(123L);
		profile.setGithubUsername("oldusername");
		profile.setUsername("oldusername");
		teamRepository.save(profile);

		teamImporter.importTeamMembers(gitHub);

		MemberProfile updatedProfile = teamRepository.findByGithubId(123L);
		assertThat(updatedProfile, not(nullValue()));
		assertThat(updatedProfile.getGithubUsername(), equalTo("jdoe"));
		assertThat(updatedProfile.getUsername(), equalTo("oldusername"));
		assertThat(updatedProfile.isHidden(), equalTo(false));
	}

	@Test
	public void importHidesActiveMembersNoLongerOnTheTeam() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setGithubId(456L);
		profile.setGithubUsername("quitter");
		profile.setUsername("quitter");
		profile.setHidden(false);
		teamRepository.save(profile);

		teamImporter.importTeamMembers(gitHub);

		MemberProfile updatedProfile = teamRepository.findByGithubId(456L);
		assertThat(updatedProfile, not(nullValue()));
		assertThat(updatedProfile.getGithubUsername(), equalTo("quitter"));
		assertThat(updatedProfile.isHidden(), equalTo(true));
	}

	@Test
	public void importHidesActiveMembersNoLongerOnTheTeamWithoutAGithubId() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setGithubUsername("quitter");
		profile.setUsername("quitter");
		profile.setHidden(false);
		teamRepository.save(profile);

		teamImporter.importTeamMembers(gitHub);

		MemberProfile updatedProfile = teamRepository.findByUsername("quitter");
		assertThat(updatedProfile, not(nullValue()));
		assertThat(updatedProfile.isHidden(), equalTo(true));
	}

}
