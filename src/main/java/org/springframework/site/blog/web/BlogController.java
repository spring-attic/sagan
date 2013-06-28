package org.springframework.site.blog.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.PaginationInfo;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostCategory;
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
		model.addAttribute("post", service.getPublishedPost(postId));
		model.addAttribute("categories", PostCategory.values());
		return "blog/show";
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String listPublishedPosts(Model model, @RequestParam(defaultValue = "1") int page) {
		Pageable pageRequest = BlogPostsPageRequest.forLists(page);
		Page<Post> result = service.getPublishedPosts(pageRequest);
		return renderListOfPosts(result, model);
	}

	@RequestMapping(value = "/category/{category}", method = { GET, HEAD })
	public String listPublishedPostsForCategory(@PathVariable PostCategory category, Model model, @RequestParam(defaultValue = "1") int page) {
		Pageable pageRequest = BlogPostsPageRequest.forLists(page);
		Page<Post> result = service.getPublishedPosts(category, pageRequest);
		return renderListOfPosts(result, model);
	}

	@RequestMapping(value = "/broadcasts", method = { GET, HEAD })
	public String listPublishedBroadcasts(Model model, @RequestParam(defaultValue = "1") int page) {
		Pageable pageRequest = BlogPostsPageRequest.forLists(page);
		Page<Post> result = service.getPublishedBroadcastPosts(pageRequest);
		return renderListOfPosts(result, model);
	}

	private String renderListOfPosts(Page<Post> page, Model model) {
		List<Post> posts = page.getContent();
		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("posts", posts);
		model.addAttribute("paginationInfo", new PaginationInfo(page));
		return "blog/index";
	}
}
