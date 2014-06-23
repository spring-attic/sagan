package sagan.team.support;

import sagan.support.github.GitHubClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
class DefaultTeamImporter implements TeamImporter {

    private static final Log logger = LogFactory.getLog(DefaultTeamImporter.class);

    private final TeamService teamService;
    private final GitHubClient gitHub;
    private final String gitHubTeamId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public DefaultTeamImporter(TeamService teamService, GitHubClient gitHub,
                               @Value("${github.team.id}") String gitHubTeamId) {
        this.teamService = teamService;
        this.gitHub = gitHub;
        this.gitHubTeamId = gitHubTeamId;
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
        try {
            ResponseEntity<GitHubUser[]> entity =
                    gitHub.restOperations().getForEntity(membersUrl, GitHubUser[].class, gitHubTeamId);
            return entity.getBody();
        } catch (RestClientException e) {
            logger.warn("Failed to retrieve team members. reason=" + e.getMessage());
            return new GitHubUser[] { };
        }
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
