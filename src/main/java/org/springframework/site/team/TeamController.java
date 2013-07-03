package org.springframework.site.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.blog.web.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/about/team")
public class TeamController {

	@Autowired
	private TeamRepository teamRepository;

	@RequestMapping(value = "/{memberId:\\w+}", method = {GET, HEAD})
	public String showProfile(@PathVariable String memberId, Model model){
		MemberProfile profile = teamRepository.findByMemberId(memberId);
		if (profile == null) {
			throw new EntityNotFoundException("Profile not found with Id=" + memberId);
		}
		model.addAttribute("profile", profile);
		return "team/show";
	}

}
