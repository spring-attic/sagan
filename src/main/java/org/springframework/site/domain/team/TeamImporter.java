package org.springframework.site.domain.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.services.github.GitHubService;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamImporter {

	@Autowired
	private GitHubService gitHubService;

	@Autowired
	private TeamService teamService;

	@Transactional
	public void importTeamMembers() {
		List<GitHubUser> users = gitHubService.getOrganizationUsers("springframework-meta");
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
}
