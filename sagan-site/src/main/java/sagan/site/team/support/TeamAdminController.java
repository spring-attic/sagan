package sagan.site.team.support;

import java.security.Principal;

import sagan.site.team.MemberProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller that handles administrative team actions, e.g. editing member details and
 * synchronizing information from the GitHub team where membership is actually managed.
 * Per rules in {@code sagan.SecurityConfig}, authentication is required for all requests.
 * See {@link TeamController} for public, read-only operations.
 */
@Controller
class TeamAdminController {

    private final TeamService teamService;

    @Autowired
    public TeamAdminController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/admin/team")
    public String getTeamAdminPage(Model model) {
        model.addAttribute("activeMembers", teamService.fetchActiveMembers());
        model.addAttribute("hiddenMembers", teamService.fetchHiddenMembers());
        return "admin/team/index";
    }

    @GetMapping("/admin/profile")
    public String editProfileForm(Principal principal, Model model) {
		MemberProfile profile = this.teamService.fetchMemberProfile(Long.parseLong(principal.getName()))
				.orElseThrow(() -> new MemberNotFoundException(principal.getName()));
        model.addAttribute("profile", profile);
        model.addAttribute("formAction", "/admin/profile");
        return "admin/team/edit";
    }

    @GetMapping("/admin/team/{username}")
    public String editTeamMemberForm(@PathVariable("username") String username, Model model) {
		MemberProfile profile = this.teamService.fetchMemberProfile(username)
				.orElseThrow(() -> new MemberNotFoundException(username));
        model.addAttribute("profile", profile);
        model.addAttribute("formAction", "/admin/team/" + username);
        return "admin/team/edit";
    }

    @PostMapping(value = "/admin/profile")
    public String saveProfile(Principal principal, MemberProfile profile) {
        teamService.updateMemberProfile(new Long(principal.getName()), profile);
        return "redirect:/admin/profile";
    }

    @PostMapping(value = "/admin/team/{username}")
    public String saveTeamMember(@PathVariable("username") String username, MemberProfile profile) {
        teamService.updateMemberProfile(username, profile);
        return "redirect:/admin/team/" + username;
    }

}
