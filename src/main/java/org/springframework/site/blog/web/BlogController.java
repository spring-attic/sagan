package org.springframework.site.blog.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.site.blog.BlogService;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.PaginationInfo;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostCategory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/blog")
public class BlogController {

	private final BlogService service;
	private final PostViewFactory postViewFactory;

	@Autowired
	public BlogController(BlogService service, PostViewFactory postViewFactory) {
		this.service = service;
		this.postViewFactory = postViewFactory;
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = { GET, HEAD })
	public String showPost(@PathVariable Long postId, @PathVariable String slug, Model model) {
		model.addAttribute("post", service.getPublishedPost(postId));
		model.addAttribute("categories", PostCategory.values());
		return "blog/show";
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String listPublishedPosts(Model model, @RequestParam(defaultValue = "1") int page, HttpServletRequest request) {
		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = service.getPublishedPosts(pageRequest);
		return renderListOfPosts(result, model, request);
	}

	@RequestMapping(value = "/category/{category}", method = { GET, HEAD })
	public String listPublishedPostsForCategory(@PathVariable PostCategory category, Model model, @RequestParam(defaultValue = "1") int page, HttpServletRequest request) {
		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = service.getPublishedPosts(category, pageRequest);
		return renderListOfPosts(result, model, request);
	}

	@RequestMapping(value = "/broadcasts", method = { GET, HEAD })
	public String listPublishedBroadcasts(Model model, @RequestParam(defaultValue = "1") int page, HttpServletRequest request) {
		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = service.getPublishedBroadcastPosts(pageRequest);
		return renderListOfPosts(result, model, request);
	}

	private String renderListOfPosts(Page<Post> page, Model model, HttpServletRequest request) {
		Page<PostView> postViewPage = postViewFactory.createPostViewPage(page);
		List<PostView> posts = postViewPage.getContent();
		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("posts", posts);
		model.addAttribute("paginationInfo", new PaginationInfo(postViewPage));
		String feedPath = request.getServletPath().replaceAll("/$", "");
		model.addAttribute("feed_path", feedPath + ".atom");
		return "blog/index";
	}
}
