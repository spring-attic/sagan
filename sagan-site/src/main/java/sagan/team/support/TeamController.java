package sagan.team.support;

import sagan.blog.Post;
import sagan.blog.support.BlogService;
import sagan.blog.support.PostView;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;
import sagan.team.MemberProfile;
import sagan.team.TeamLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles read-only team actions, e.g. listing all team members at /team
 * and showing individual team members at /team/{member}. See {@link TeamAdminController}
 * for administrative operations.
 */
@Controller
@RequestMapping("/team")
class TeamController {

    private final TeamService teamService;
    private final BlogService blogService;
    private final DateFactory dateFactory;

    @Autowired
    public TeamController(TeamService teamService, BlogService blogService, DateFactory dateFactory) {
        this.teamService = teamService;
        this.blogService = blogService;
        this.dateFactory = dateFactory;
    }

    @RequestMapping(method = { GET, HEAD })
    public String showTeam(Model model) {
        List<MemberProfile> profiles = teamService.fetchActiveMembers();
        model.addAttribute("profiles", profiles);
        model.addAttribute("teamLocations", profiles.stream()
                .filter(profile -> profile.getTeamLocation() != null)
                .map(MemberProfile::getTeamLocation)
                .collect(Collectors.toList()));
        return "team/index";
    }

    @RequestMapping(value = "/{username}", method = { GET, HEAD })
    public String showProfile(@PathVariable String username, Model model) {
        MemberProfile profile = teamService.fetchMemberProfileUsername(username);
        if (profile == MemberProfile.NOT_FOUND) {
            throw new MemberNotFoundException(username);
        }
        if (profile.isHidden()) {
            throw new MemberNotFoundException("Member profile for username '%s' is hidden", username);
        }
        model.addAttribute("profile", profile);
        Page<Post> posts = blogService.getPublishedPostsForMember(profile, PageableFactory.forLists(1));
        Page<PostView> postViewPage = PostView.pageOf(posts, dateFactory);
        model.addAttribute("posts", postViewPage);

        List<TeamLocation> teamLocations = new ArrayList<>();
        if (profile.getTeamLocation() != null) {
            teamLocations.add(profile.getTeamLocation());
        }
        model.addAttribute("teamLocations", teamLocations);

        return "team/show";
    }
}
