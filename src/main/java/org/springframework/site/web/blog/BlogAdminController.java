package org.springframework.site.web.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.blog.PostForm;
import org.springframework.site.web.PageableFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping("/admin/blog")
public class BlogAdminController {

	private BlogService service;
	private PostViewFactory postViewFactory;

	@Autowired
	public BlogAdminController(BlogService service, PostViewFactory postViewFactory) {
		this.service = service;
		this.postViewFactory = postViewFactory;
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String dashboard(Model model) {
		Pageable pageRequest = PageableFactory.forDashboard();
		model.addAttribute("posts", postViewFactory.createPostViewPage(service.getPublishedPosts(pageRequest)));
		model.addAttribute("drafts", postViewFactory.createPostViewPage(service.getDraftPosts(pageRequest)));
		model.addAttribute("scheduled", postViewFactory.createPostViewPage(service.getScheduledPosts(pageRequest)));
		return "admin/blog/index";
	}

	@RequestMapping(value = "/new", method = { GET, HEAD })
	public String newPost(Model model) {
		model.addAttribute("postForm", new PostForm());
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
		model.addAttribute("post", postViewFactory.createPostView(service.getPost(postId)));
		return "admin/blog/show";
	}

	@RequestMapping(value = "", method = { POST })
	public String createPost(Principal principal, @Valid PostForm postForm, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("categories", PostCategory.values());
			return "admin/blog/new";
		} else {
			Post post = service.addPost(postForm, principal.getName());
			PostView postView = postViewFactory.createPostView(post);
			return "redirect:" + postView.getPath();
		}
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = PUT)
	public String updatePost(@PathVariable Long postId, @Valid PostForm postForm, BindingResult bindingResult, Model model) {
		Post post = service.getPost(postId);
		if (bindingResult.hasErrors()) {
			model.addAttribute("categories", PostCategory.values());
			model.addAttribute("post", post);
			return "admin/blog/edit";
		} else {
			service.updatePost(post, postForm);
			PostView postView = postViewFactory.createPostView(post);
			return "redirect:" + postView.getPath();
		}
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = DELETE)
	public String deletePost(@PathVariable Long postId) {
		Post post = service.getPost(postId);
		service.deletePost(post);
		return "redirect:/admin/blog";
	}

	@RequestMapping(value = "reindex", method = POST)
	public String reIndexAllBlogPosts() {
		service.reIndexAllPosts();
		return "redirect:/admin/blog";
	}

}
