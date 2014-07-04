package sagan.team.support;

import sagan.support.github.GitHubClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
class DefaultTeamImporter implements TeamImporter {

    private final TeamService teamService;
    private final GitHubClient gitHub;
    private final String gitHubTeamId;
    private final ObjectMapper objectMapper;

    @Autowired
    public DefaultTeamImporter(TeamService teamService, GitHubClient gitHub,
                               @Value("${github.team.id}") String gitHubTeamId,
                               ObjectMapper objectMapper) {
        this.teamService = teamService;
        this.gitHub = gitHub;
        this.gitHubTeamId = gitHubTeamId;
        this.objectMapper = objectMapper;
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
                gitHub.restOperations().getForEntity(membersUrl, GitHubUser[].class, gitHubTeamId);
        return entity.getBody();
    }

    public String getNameForUser(String username) {
        String jsonResponse = gitHub.sendRequestForJson("/users/{user}", username);
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return jsonNode.get("name").asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
