package org.springframework.site.web.migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostForm;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class MigrationController {

	private final TeamService teamService;
	private final BlogService blogService;

	@Autowired
	public MigrationController(TeamService teamService, BlogService blogService) {
		this.teamService = teamService;
		this.blogService = blogService;
	}

	@RequestMapping(value = "/migration/profile", method = POST)
	public void migrateTeamMember(HttpServletResponse response, MemberProfile profile) {
		MemberProfile memberProfile = teamService.fetchMemberProfile(profile.getMemberId());
		if (memberProfile == null) {
			teamService.saveMemberProfile(profile);
		}
		response.setContentLength(0);
	}

	@RequestMapping(value = "/migration/blogpost", method = POST)
	public void migrateBlogPost(HttpServletResponse response, PostForm postForm) {
		Post post = blogService.getPost(postForm.getTitle(), postForm.getPublishAt());
		if (post == null) {
			blogService.addPost(postForm, "someguy");
		} else {
			blogService.updatePost(post, postForm);
		}
		response.setContentLength(0);
	}

}
