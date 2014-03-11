package sagan.team.support;

import sagan.support.github.GitHubClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
class DefaultTeamImporter implements TeamImporter {

    private static final String SPRING_TEAM_MEMBERS_ID = "482984";

    private final TeamService teamService;
    private final GitHubClient gitHub;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public DefaultTeamImporter(TeamService teamService, GitHubClient gitHub) {
        this.teamService = teamService;
        this.gitHub = gitHub;
    }

    @Transactional
    public void importTeamMembers(GitHub gitHub) {
        GitHubUser[] users = getGitHubUsers(gitHub);
        List<Long> userIds = new ArrayList<>();
        for (GitHubUser user : users) {
            userIds.add(user.getId());
            String userName = getNameForUser(user.getLogin());

            teamService.createOrUpdateMemberProfile(user.getId(), user.getLogin(), user.getAvatarUrl(), userName);
        }
        teamService.showOnlyTeamMembersWithIds(userIds);
    }

    private GitHubUser[] getGitHubUsers(GitHub gitHub) {
        String membersUrl = GitHubClient.API_URL_BASE + "/teams/{teamId}/members";
        ResponseEntity<GitHubUser[]> entity =
                gitHub.restOperations().getForEntity(membersUrl, GitHubUser[].class, SPRING_TEAM_MEMBERS_ID);
        return entity.getBody();
    }

    public String getNameForUser(String username) {
        String jsonResponse = gitHub.sendRequestForJson("/users/{user}", username);
        try {
            Map<String, String> map = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {
            });
            return map.get("name");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
