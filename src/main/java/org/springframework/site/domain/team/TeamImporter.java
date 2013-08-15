package org.springframework.site.domain.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.services.github.GitHubService;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamImporter {

	private GitHubService gitHubService;
	private TeamService teamService;

	@Autowired
	public TeamImporter(GitHubService gitHubService, TeamService teamService) {
		this.gitHubService = gitHubService;
		this.teamService = teamService;
	}

	public void importTeamMembers() {
		List<GitHubUser> users = gitHubService.getOrganizationUsers("springframework-meta");
		List<Long> userIds = new ArrayList<>();
		for (GitHubUser user : users) {
			userIds.add(user.getId());
			teamService.createOrUpdateMemberProfile(user.getId(),
					user.getLogin(),
					user.getAvatarUrl(),
					user.getName());
		}
		teamService.showOnlyTeamMembersWithIds(userIds);
	}
}
