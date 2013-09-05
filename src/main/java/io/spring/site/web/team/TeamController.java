package io.spring.site.web.team;

import io.spring.site.domain.blog.Post;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.TeamLocation;
import io.spring.site.web.PageableFactory;
import io.spring.site.web.blog.CachedBlogService;
import io.spring.site.web.blog.EntityNotFoundException;
import io.spring.site.web.blog.PostView;
import io.spring.site.web.blog.PostViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    private final CachedTeamService teamService;
    private final CachedBlogService blogService;
    private final PostViewFactory postViewFactory;

    @Autowired
    public TeamController(CachedTeamService teamService,
                          CachedBlogService blogService,
                          PostViewFactory postViewFactory) {
        this.teamService = teamService;
        this.blogService = blogService;
        this.postViewFactory = postViewFactory;
    }

    @RequestMapping(value = "", method = {GET, HEAD})
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


    @RequestMapping(value = "/{username}", method = {GET, HEAD})
    public String showProfile(@PathVariable String username, Model model){
        MemberProfile profile = teamService.fetchMemberProfileUsername(username);
        if (profile == MemberProfile.NOT_FOUND) {
            throw new EntityNotFoundException("Profile not found with Id=" + username);
        }
        if (profile.isHidden()) {
            throw new EntityNotFoundException("Profile not active: " + username);
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
