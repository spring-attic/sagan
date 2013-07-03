package org.springframework.site.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
public class TeamAdminController {

	@Autowired
	private TeamRepository teamRepository;

	@RequestMapping(value = "/admin/profile/edit", method = {GET, HEAD})
	public String editTeamMember(Principal principal, Model model) {
		MemberProfile profile = teamRepository.findByMemberId(principal.getName());
		if (profile == null) {
			profile = new MemberProfile();
			profile.setGithubUsername(principal.getName());
		}
		model.addAttribute("profile", profile);
		return "admin/team/edit";
	}


	@RequestMapping(value = "/admin/profile", method = PUT)
	public String saveTeamMember(Principal principal, MemberProfile profile, Model model) {

		MemberProfile existingProfile = teamRepository.findByMemberId(principal.getName());
		if (existingProfile != null) {
			existingProfile.setSpeakerdeckUsername(profile.getSpeakerdeckUsername());
			existingProfile.setGravatarEmail(profile.getGravatarEmail());
			existingProfile.setTwitterUsername(profile.getTwitterUsername());
			existingProfile.setBio(profile.getBio());
			existingProfile.setFirstName(profile.getFirstName());
			existingProfile.setGithubUsername(profile.getGithubUsername());
			existingProfile.setTwitterUsername(profile.getTwitterUsername());
			existingProfile.setSpeakerdeckUsername(profile.getSpeakerdeckUsername());
			existingProfile.setLanyrdUsername(profile.getLanyrdUsername());
			existingProfile.setLastName(profile.getLastName());
			existingProfile.setLocation(profile.getLocation());
			teamRepository.save(existingProfile);
		} else {
			profile.setMemberId(principal.getName());
			model.addAttribute("profile", profile);
			teamRepository.save(profile);
		}

		return "redirect:/admin/profile/edit";
	}
}
