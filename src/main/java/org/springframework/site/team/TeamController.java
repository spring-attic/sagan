package org.springframework.site.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.web.EntityNotFoundException;
import org.springframework.site.blog.web.PostView;
import org.springframework.site.blog.web.PostViewFactory;
import org.springframework.site.web.PageableFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/about/team")
public class TeamController {

	private TeamRepository teamRepository;
	private BlogService blogService;
	private PostViewFactory postViewFactory;

	@Autowired
	public TeamController(TeamRepository teamRepository, BlogService blogService, PostViewFactory postViewFactory) {
		this.teamRepository = teamRepository;
		this.blogService = blogService;
		this.postViewFactory = postViewFactory;
	}

	@RequestMapping(value = "/{memberId:\\w+}", method = {GET, HEAD})
	public String showProfile(@PathVariable String memberId, Model model){
		MemberProfile profile = teamRepository.findByMemberId(memberId);
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
