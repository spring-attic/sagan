package org.springframework.site.web.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public class TeamAdminController {

	private final TeamService teamService;

	@Autowired
	public TeamAdminController(TeamService teamService) {
		this.teamService = teamService;
	}

	@RequestMapping(value = "/admin/profile", method = {GET, HEAD})
	public String editProfile(Principal principal, Model model) {
		MemberProfile profile = teamService.fetchMemberProfile(principal.getName());
		model.addAttribute("profile", profile);
		model.addAttribute("formAction", "/admin/profile");
		return "admin/team/edit";
	}

	@RequestMapping(value = "/admin/team/{memberId:\\w+}", method = {GET, HEAD})
	public String editTeamMember(@PathVariable("memberId") String memberId, Model model) {
		MemberProfile profile = teamService.fetchMemberProfile(memberId);
		model.addAttribute("profile", profile);
		model.addAttribute("formAction", "/admin/team/" + memberId);
		return "admin/team/edit";
	}


	@RequestMapping(value = "/admin/profile", method = PUT)
	public String saveProfile(Principal principal, MemberProfile profile) {
		teamService.updateMemberProfile(principal.getName(), profile);
		return "redirect:/admin/profile";
	}

	@RequestMapping(value = "/admin/team/{memberId:\\w+}", method = PUT)
	public String saveTeamMember(@PathVariable("memberId") String memberId, MemberProfile profile) {
		teamService.updateMemberProfile(memberId, profile);
		return "redirect:/admin/team/" + memberId;
	}

}
