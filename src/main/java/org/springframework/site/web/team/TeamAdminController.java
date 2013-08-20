package org.springframework.site.web.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamImporter;
import org.springframework.site.domain.team.TeamService;
import org.springframework.site.web.blog.EntityNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public class TeamAdminController {

	private final TeamService teamService;
	private final TeamImporter teamImporter;
	private final InMemoryUsersConnectionRepository usersConnectionRepository;

	@Autowired
	public TeamAdminController(TeamService teamService, TeamImporter teamImporter, InMemoryUsersConnectionRepository usersConnectionRepository) {
		this.teamService = teamService;
		this.teamImporter = teamImporter;
		this.usersConnectionRepository = usersConnectionRepository;
	}

	@RequestMapping(value = "/admin/team", method = {GET, HEAD})
	public String getTeamAdminPage(Model model) {
		model.addAttribute("members", teamService.fetchAllProfiles());
		return "admin/team/index";
	}

	@RequestMapping(value = "/admin/profile", method = {GET, HEAD})
	public String editProfileForm(Principal principal, Model model) {
		MemberProfile profile = teamService.fetchMemberProfile(new Long(principal.getName()));
		model.addAttribute("profile", profile);
		model.addAttribute("formAction", "/admin/profile");
		return "admin/team/edit";
	}

	@RequestMapping(value = "/admin/team/{username}", method = {GET, HEAD})
	public String editTeamMemberForm(@PathVariable("username") String username, Model model) {
		MemberProfile profile = teamService.fetchMemberProfileUsername(username);
		if (profile == null) {
			throw new EntityNotFoundException("Profile not found with Id=" + username);
		}
		model.addAttribute("profile", profile);
		model.addAttribute("formAction", "/admin/team/" + username);
		return "admin/team/edit";
	}


	@RequestMapping(value = "/admin/profile", method = PUT)
	public String saveProfile(Principal principal, MemberProfile profile) {
		teamService.updateMemberProfile(new Long(principal.getName()), profile);
		return "redirect:/admin/profile";
	}

	@RequestMapping(value = "/admin/team/{username}", method = PUT)
	public String saveTeamMember(@PathVariable("username") String username, MemberProfile profile) {
		teamService.updateMemberProfile(username, profile);
		return "redirect:/admin/team/" + username;
	}

	@RequestMapping(value = "/admin/team/github_import", method = POST)
	public String importTeamMembersFromGithub(Principal principal) {
		GitHub gitHub = getGitHub(principal);
		teamImporter.importTeamMembers(gitHub);
		return "redirect:/admin/team";
	}

	private GitHub getGitHub(Principal principal) {
		MemberProfile profile = teamService.fetchMemberProfile(new Long(principal.getName()));
		String githubId = profile.getGithubId().toString();
		ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(githubId);
		Connection<GitHub> connection = connectionRepository.findPrimaryConnection(GitHub.class);
		if (connection != null) {
			return connection.getApi();
		}
		throw new RuntimeException("Unable to obtain GitHub connection");
	}

}
