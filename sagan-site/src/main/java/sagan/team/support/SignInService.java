package sagan.team.support;

import sagan.team.MemberProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.stereotype.Service;

@Service
public class SignInService {

    private static final String IS_MEMBER_URL = "https://api.github.com/teams/{team}/members/{user}";
    private final TeamService teamService;
    private final String gitHubTeamId;

    @Autowired
    public SignInService(TeamService teamService, @Value("${github.team.id}") String gitHubTeamId) {
        this.teamService = teamService;
        this.gitHubTeamId = gitHubTeamId;
    }

    public MemberProfile getOrCreateMemberProfile(Long githubId, GitHub gitHub) {
        GitHubUserProfile remoteProfile = gitHub.userOperations().getUserProfile();

        return teamService.createOrUpdateMemberProfile(githubId, remoteProfile.getUsername(), remoteProfile
                .getProfileImageUrl(), remoteProfile.getName());
    }

    public boolean isSpringMember(String userId, GitHub gitHub) {
        ResponseEntity<Void> response =
                gitHub.restOperations().getForEntity(IS_MEMBER_URL, Void.class, gitHubTeamId, userId);
        return response.getStatusCode() == HttpStatus.NO_CONTENT;
    }
}
