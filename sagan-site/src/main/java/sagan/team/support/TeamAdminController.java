package sagan.team.support;

import sagan.team.MemberProfile;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles administrative team actions, e.g. editing member details and
 * synchronizing information from the GitHub team where membership is actually managed.
 * Per rules in {@code sagan.SecurityConfig}, authentication is required for all requests.
 * See {@link TeamController} for public, read-only operations.
 */
@Controller
class TeamAdminController {

    private final TeamService teamService;
    private final TeamImporter teamImporter;

    @Autowired
    public TeamAdminController(TeamService teamService, TeamImporter teamImporter) {
        this.teamService = teamService;
        this.teamImporter = teamImporter;
    }

    @RequestMapping(value = "/admin/team", method = { GET, HEAD })
    public String getTeamAdminPage(Model model) {
        model.addAttribute("activeMembers", teamService.fetchActiveMembers());
        model.addAttribute("hiddenMembers", teamService.fetchHiddenMembers());
        return "admin/team/index";
    }

    @RequestMapping(value = "/admin/profile", method = { GET, HEAD })
    public String editProfileForm(Principal principal, Model model) {
        MemberProfile profile = teamService.fetchMemberProfile(principal.getName());
        model.addAttribute("profile", profile);
        model.addAttribute("formAction", "/admin/profile");
        return "admin/team/edit";
    }

    @RequestMapping(value = "/admin/team/{username}", method = { GET, HEAD })
    public String editTeamMemberForm(@PathVariable("username") String username, Model model) {
        MemberProfile profile = teamService.fetchMemberProfileUsername(username);
        if (profile == MemberProfile.NOT_FOUND) {
            throw new MemberNotFoundException(username);
        }
        model.addAttribute("profile", profile);
        model.addAttribute("formAction", "/admin/team/" + username);
        return "admin/team/edit";
    }

    @RequestMapping(value = "/admin/profile", method = PUT)
    public String saveProfile(Principal principal, MemberProfile profile) {
        teamService.updateMemberProfile(principal.getName(), profile);
        return "redirect:/admin/profile";
    }

    @RequestMapping(value = "/admin/team/{username}", method = PUT)
    public String saveTeamMember(@PathVariable("username") String username, MemberProfile profile) {
        teamService.updateMemberProfile(username, profile);
        return "redirect:/admin/team/" + username;
    }

    @RequestMapping(value = "/admin/team/github_import", method = POST)
    public String importTeamMembersFromGithub(Principal principal) {
        teamImporter.importTeamMembers();
        return "redirect:/admin/team";
    }

}
