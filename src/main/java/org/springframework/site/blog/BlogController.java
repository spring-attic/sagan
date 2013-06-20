package org.springframework.site.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/blog")
public class BlogController {

	private BlogService service;

	@Autowired
	public BlogController(BlogService service) {
		this.service = service;
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = { GET, HEAD })
	public String showPost(@PathVariable Long postId, @PathVariable String slug, Model model) {
		model.addAttribute("post", service.getPost(postId));
		return "blog/show";
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String listPosts(Model model, @RequestParam(defaultValue = "1") int page) {
		PageRequest pageRequest = new BlogPostsPageRequest(page - 1);
		model.addAttribute("posts", service.mostRecentPosts(pageRequest));
		model.addAttribute("paginationInfo", service.paginationInfo(pageRequest));
		return "blog/index";
	}
}
