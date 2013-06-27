package org.springframework.site.blog.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostCategory;
import org.springframework.site.blog.PostForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/admin/blog")
public class BlogAdminController {

	private BlogService service;

	@Autowired
	public BlogAdminController(BlogService service) {
		this.service = service;
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String dashboard(Model model) {
		model.addAttribute("posts", service.getAllPosts(new PageRequest(0, 20)));
		return "admin/blog/index";
	}

	@RequestMapping(value = "/new", method = { GET, HEAD })
	public String newPost(PostForm postForm, Model model) {
		model.addAttribute("categories", PostCategory.values());
		return "admin/blog/new";
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = {GET, HEAD})
	public String showPost(@PathVariable Long postId, @PathVariable String slug, Model model) {
		model.addAttribute("post", service.getPost(postId));
		return "admin/blog/show";
	}

	@RequestMapping(value = "", method = { POST })
	public String createPost(PostForm postForm) {
		Post newPost = service.addPost(postForm);
		return newPost.isDraft() ?
				"redirect:/admin" + newPost.getPath() :
				"redirect:" + newPost.getPath();
	}

}
