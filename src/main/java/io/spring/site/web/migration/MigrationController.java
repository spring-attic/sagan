package io.spring.site.web.migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.spring.site.domain.blog.BlogService;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostForm;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.TeamService;
import io.spring.site.web.SiteUrl;
import io.spring.site.web.blog.PostView;
import io.spring.site.web.blog.PostViewFactory;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/migration")
public class MigrationController {

	private SiteUrl siteUrl;
	private final PostViewFactory postViewFactory;

	private final TeamService teamService;
	private final BlogService blogService;

	@Autowired
	public MigrationController(TeamService teamService, BlogService blogService, SiteUrl siteUrl, PostViewFactory postViewFactory) {
		this.teamService = teamService;
		this.blogService = blogService;
		this.siteUrl = siteUrl;
		this.postViewFactory = postViewFactory;
	}

	@RequestMapping(value = "/profile", method = POST)
	public void migrateTeamMember( HttpServletResponse response, MemberProfile profile) {
		MemberProfile existingProfile = teamService.fetchMemberProfileUsername(profile.getUsername());

		if (existingProfile == null) {
			profile.setHidden(true);
			teamService.saveMemberProfile(profile);
			response.setStatus(201);
		} else {
			response.setStatus(200);
		}

		response.setContentLength(0);
		response.setHeader("Location", siteUrl.getAbsoluteUrl("/team/" + profile.getUsername()));
	}

	@RequestMapping(value = "/blogpost/{username}", method = POST)
	public void migrateBlogPost(@PathVariable(value="username") String username, HttpServletResponse response, @Valid PostForm postForm, BindingResult result) throws IOException {
		if (result.hasErrors()) {
			response.setStatus(400);
			for (ObjectError error : result.getAllErrors()) {
				response.getWriter().println(error.toString());
			}
		} else {

			Post post = blogService.getPost(postForm.getTitle(), postForm.getCreatedAt());
			response.setContentLength(0);
			if (post == null) {
				post = blogService.addPost(postForm, username);
				response.setStatus(201);
			} else {
				blogService.updatePost(post, postForm);
				response.setStatus(200);
			}
			PostView postView = postViewFactory.createPostView(post);
			response.setHeader("Location", siteUrl.getAbsoluteUrl(postView.getPath()));
		}
	}

	@RequestMapping(value = "/team_members", method = GET)
	public @ResponseBody Map<String, String> migrateBlogPost() throws IOException {
		Map<String, String> authors = new HashMap<>();
		for (MemberProfile profile : teamService.fetchAllProfiles()) {
			authors.put(profile.getFullName(), profile.getUsername());
		}
		authors.put("Some Guy", "someguy");
		return authors;
	}

}
