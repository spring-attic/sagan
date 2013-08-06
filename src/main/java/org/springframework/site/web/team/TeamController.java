package org.springframework.site.web.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamLocation;
import org.springframework.site.domain.team.TeamService;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.blog.EntityNotFoundException;
import org.springframework.site.web.blog.PostView;
import org.springframework.site.web.blog.PostViewFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/team")
public class TeamController {

	private final TeamService teamService;
	private final BlogService blogService;
	private final PostViewFactory postViewFactory;

	@Autowired
	public TeamController(TeamService teamService,
						  BlogService blogService,
						  PostViewFactory postViewFactory) {
		this.teamService = teamService;
		this.blogService = blogService;
		this.postViewFactory = postViewFactory;
	}

	@RequestMapping(value = "", method = {GET, HEAD})
	public String showTeam(Model model) throws IOException {
		List<MemberProfile> profiles = teamService.fetchVisibleMembers();
		model.addAttribute("profiles", profiles);
		List<TeamLocation> teamLocations = new ArrayList<>();
		for (MemberProfile profile : profiles) {
			if (profile.getTeamLocation() != null) {
				teamLocations.add(profile.getTeamLocation());
			}
		}
		model.addAttribute("teamLocations", teamLocations);
		return "team/index";
	}


	@RequestMapping(value = "/{memberId:\\w+}", method = {GET, HEAD})
	public String showProfile(@PathVariable String memberId, Model model){
		MemberProfile profile = teamService.fetchMemberProfile(memberId);
		if (profile == null) {
			throw new EntityNotFoundException("Profile not found with Id=" + memberId);
		}
		model.addAttribute("profile", profile);
		Page<Post> posts = blogService.getPublishedPostsForMember(profile, PageableFactory.forLists(1));
		Page<PostView> postViewPage = postViewFactory.createPostViewPage(posts);
		model.addAttribute("posts", postViewPage);

		return "team/show";
	}

}
