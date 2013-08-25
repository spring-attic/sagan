package org.springframework.site.domain.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.site.domain.services.github.GitHubService;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamImporter {

	private static final String SPRING_TEAM_MEMBERS_ID = "482984";

	@Autowired
	private GitHubService gitHubService;

	@Autowired
	private TeamService teamService;

	@Transactional
	public void importTeamMembers(GitHub gitHub) {
		GitHubUser[] users = getGitHubUsers(gitHub);
		List<Long> userIds = new ArrayList<>();
		for (GitHubUser user : users) {
			userIds.add(user.getId());
			String userName = gitHubService.getNameForUser(user.getLogin());

			teamService.createOrUpdateMemberProfile(user.getId(),
					user.getLogin(),
					user.getAvatarUrl(),
					userName);
		}
		teamService.showOnlyTeamMembersWithIds(userIds);
	}

	private GitHubUser[] getGitHubUsers(GitHub gitHub) {
		String membersUrl = GitHubService.API_URL_BASE + "/teams/{teamId}/members";
		ResponseEntity<GitHubUser[]> entity = gitHub.restOperations().getForEntity(membersUrl, GitHubUser[].class, SPRING_TEAM_MEMBERS_ID);
		return entity.getBody();
	}
}
