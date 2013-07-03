package org.springframework.site.blog.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostCategory;
import org.springframework.site.blog.PostForm;
import org.springframework.site.blog.PostSearchEntryMapper;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.PageableFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Date;

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
	private SearchService searchService;

	private PostSearchEntryMapper mapper = new PostSearchEntryMapper();

	@Autowired
	public BlogAdminController(BlogService service, PostViewFactory postViewFactory, SearchService searchService) {
		this.service = service;
		this.postViewFactory = postViewFactory;
		this.searchService = searchService;
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
	public String createPost(PostForm postForm, Principal principal) {
		Post post = service.addPost(postForm, principal.getName());
		saveToIndex(post);
		PostView postView = postViewFactory.createPostView(post);
		return "redirect:" + postView.getPath();
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = PUT)
	public String updatePost(@PathVariable Long postId, PostForm postForm) {
		Post post = service.getPost(postId);
		service.updatePost(post, postForm);
		saveToIndex(post);
		PostView postView = postViewFactory.createPostView(post);
		return "redirect:" + postView.getPath();
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = DELETE)
	public String deletePost(@PathVariable Long postId) {
		Post post = service.getPost(postId);
		service.deletePost(post);
		return "redirect:/admin/blog";
	}

	private void saveToIndex(Post post) {
		if (post.isLiveOn(new Date())) {
			searchService.saveToIndex(mapper.map(post));
		}
	}

}
