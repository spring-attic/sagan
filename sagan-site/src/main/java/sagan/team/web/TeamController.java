package sagan.team.web;

import sagan.blog.Post;
import sagan.blog.service.BlogService;
import sagan.blog.view.PostView;
import sagan.blog.view.PostViewFactory;
import sagan.team.MemberProfile;
import sagan.team.TeamLocation;
import sagan.team.service.CachedTeamService;
import sagan.util.web.PageableFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller handling HTTP requests for the team section of the site.
 * 
 * @author Pivotal Labs
 * @author Chris Beams
 */
@Controller
@RequestMapping("/team")
public class TeamController {

    private final CachedTeamService teamService;
    private final BlogService blogService;
    private final PostViewFactory postViewFactory;

    @Autowired
    public TeamController(CachedTeamService teamService, BlogService blogService, PostViewFactory postViewFactory) {
        this.teamService = teamService;
        this.blogService = blogService;
        this.postViewFactory = postViewFactory;
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String showTeam(Model model) throws IOException {
        List<MemberProfile> profiles = teamService.fetchActiveMembers();
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

    @RequestMapping(value = "/{username}", method = { GET, HEAD })
    public String showProfile(@PathVariable String username, Model model) {
        MemberProfile profile = teamService.fetchMemberProfileUsername(username);
        if (profile == MemberProfile.NOT_FOUND) {
            throw new MemberNotFoundException("Profile not found with Id=" + username);
        }
        if (profile.isHidden()) {
            throw new MemberNotFoundException("Profile not active: " + username);
        }
        model.addAttribute("profile", profile);
        Page<Post> posts = blogService.getPublishedPostsForMember(profile, PageableFactory.forLists(1));
        Page<PostView> postViewPage = postViewFactory.createPostViewPage(posts);
        model.addAttribute("posts", postViewPage);

        List<TeamLocation> teamLocations = new ArrayList<>();
        if (profile.getTeamLocation() != null) {
            teamLocations.add(profile.getTeamLocation());
        }
        model.addAttribute("teamLocations", teamLocations);

        return "team/show";
    }

}
