package org.springframework.site.blog.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostCategory;
import org.springframework.site.blog.PostForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

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
	public String dashboard(Model model, @RequestParam(defaultValue = "1") int page) {
		PageRequest pageRequest = new PageRequest(page - 1, Integer.MAX_VALUE, Sort.Direction.DESC, "createdDate");
		model.addAttribute("posts", service.getPagedPublishedPosts(pageRequest));
		model.addAttribute("drafts", service.getDraftPosts(pageRequest));
		return "admin/blog/index";
	}

	@RequestMapping(value = "/new", method = { GET, HEAD })
	public String newPost(PostForm postForm, Model model) {
		model.addAttribute("categories", PostCategory.values());
		return "admin/blog/new";
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}/edit", method = {GET, HEAD})
	public String editPost(@PathVariable Long postId, @PathVariable String slug, Model model) {
		Post post = service.getPost(postId);
		PostForm postForm = new PostForm(post);

		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("postForm", postForm);
		model.addAttribute("post", post);
		return "admin/blog/edit";
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = {GET, HEAD})
	public String showPost(@PathVariable Long postId, @PathVariable String slug, Model model) {
		model.addAttribute("post", service.getPost(postId));
		return "admin/blog/show";
	}

	@RequestMapping(value = "", method = { POST })
	public String createPost(PostForm postForm, Principal principal) {
		Post newPost = service.addPost(postForm, principal.getName());
		return newPost.isDraft() ?
				"redirect:/admin" + newPost.getPath() :
				"redirect:" + newPost.getPath();
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = PUT)
	public String updatePost(@PathVariable Long postId, PostForm postForm) {
		Post post = service.getPost(postId);
		service.updatePost(post, postForm);
		return post.isDraft() ?
				"redirect:/admin" + post.getPath() :
				"redirect:" + post.getPath();
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = DELETE)
	public String deletePost(@PathVariable Long postId) {
		Post post = service.getPost(postId);
		service.deletePost(post);
		return "redirect:/admin/blog";
	}

}
