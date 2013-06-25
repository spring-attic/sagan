package org.springframework.site.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
		List<Post> posts = service.mostRecentPosts(pageRequest);
		return populatePosts(posts, model, page, pageRequest);
	}

	@RequestMapping(value = "/category/{category}", method = { GET, HEAD })
	public String listPostsForCategory(@PathVariable PostCategory category, Model model, @RequestParam(defaultValue = "1") int page) {
		PageRequest pageRequest = new BlogPostsPageRequest(page - 1);
		List<Post> posts = service.mostRecentPosts(category, pageRequest);
		return populatePosts(posts, model, page, pageRequest);
	}

	private String populatePosts(List<Post> posts, Model model, int page, PageRequest pageRequest) {
		if (posts.size() == 0 && page > 1) {
			throw new BlogPostsNotFound("Page does not exist");
		}
		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("posts", posts);
		model.addAttribute("paginationInfo", service.paginationInfo(pageRequest));
		return "blog/index";
	}
}
