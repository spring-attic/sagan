package sagan.team.support;

import sagan.support.Fixtures;
import sagan.team.MemberProfile;
import saganx.AbstractIntegrationTests;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ImportTeamFromGithubTests extends AbstractIntegrationTests {

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

        String membersJson = Fixtures.load("/fixtures/github/ghTeamInfo.json");
        GitHubUser[] gitHubUsers = mapper.readValue(membersJson, GitHubUser[].class);
        ResponseEntity<GitHubUser[]> responseEntity = new ResponseEntity<>(gitHubUsers, HttpStatus.OK);

        given(
                restOperations.getForEntity("https://api.github.com/teams/{teamId}/members?per_page=100", GitHubUser[].class,
                        "482984")).willReturn(responseEntity);

        given(restOperations.getForObject("https://api.github.com/users/{user}", GitHubUser.class, "jdoe"))
                .willReturn(mapper.readValue(Fixtures.load("/fixtures/github/ghUserProfile-jdoe.json"), GitHubUser.class));

        given(restOperations.getForObject("https://api.github.com/users/{user}", GitHubUser.class, "asmith"))
                .willReturn(mapper.readValue(Fixtures.load("/fixtures/github/ghUserProfile-asmith.json"), GitHubUser.class));
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
