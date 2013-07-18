package org.springframework.site.web.migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class MigrationController {

	private final TeamService teamService;

	@Autowired
	public MigrationController(TeamService teamService) {
		this.teamService = teamService;
	}

	@RequestMapping(value = "/migration/profile", method = POST)
	public void importTeamMember(HttpServletResponse response, MemberProfile profile) {
		MemberProfile memberProfile = teamService.fetchMemberProfile(profile.getMemberId());
		if (memberProfile == null) {
			teamService.saveMemberProfile(profile);
		}
		response.setContentLength(0);
	}

}
