package org.springframework.site.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
public class TeamAdminController {

	@Autowired
	private TeamRepository teamRepository;

	@RequestMapping(value = "/admin/profile", method = {GET, HEAD})
	public String editProfile(Principal principal, Model model) {
		MemberProfile profile = teamRepository.findByMemberId(principal.getName());
		model.addAttribute("profile", profile);
		model.addAttribute("formAction", "/admin/profile");
		return "admin/team/edit";
	}

	@RequestMapping(value = "/admin/team/{memberId:\\w+}", method = {GET, HEAD})
	public String editTeamMember(@PathVariable("memberId") String memberId, Model model) {
		MemberProfile profile = teamRepository.findByMemberId(memberId);
		model.addAttribute("profile", profile);
		model.addAttribute("formAction", "/admin/team/" + memberId);
		return "admin/team/edit";
	}


	@RequestMapping(value = "/admin/profile", method = PUT)
	public String saveProfile(Principal principal, MemberProfile profile) {
		updateMemberProfile(principal.getName(), profile);
		return "redirect:/admin/profile";
	}

	@RequestMapping(value = "/admin/team/{memberId:\\w+}", method = PUT)
	public String saveTeamMember(@PathVariable("memberId") String memberId, MemberProfile profile) {
		updateMemberProfile(memberId, profile);
		return "redirect:/admin/team/" + memberId;
	}

	private void updateMemberProfile(String memberId, MemberProfile profile) {
		MemberProfile existingProfile = teamRepository.findByMemberId(memberId);
		existingProfile.setSpeakerdeckUsername(profile.getSpeakerdeckUsername());
		existingProfile.setAvatarUrl(profile.getAvatarUrl());
		existingProfile.setTwitterUsername(profile.getTwitterUsername());
		existingProfile.setBio(profile.getBio());
		existingProfile.setName(profile.getName());
		existingProfile.setGithubUsername(profile.getGithubUsername());
		existingProfile.setTwitterUsername(profile.getTwitterUsername());
		existingProfile.setSpeakerdeckUsername(profile.getSpeakerdeckUsername());
		existingProfile.setLanyrdUsername(profile.getLanyrdUsername());
		existingProfile.setLocation(profile.getLocation());
		existingProfile.setGeoLocation(profile.getGeoLocation());
		existingProfile.setVideoEmbeds(profile.getVideoEmbeds());
		teamRepository.save(existingProfile);
	}
}
