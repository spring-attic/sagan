package org.springframework.site.web.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.web.NavSection;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.PaginationInfo;
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
@NavSection("blog")
public class BlogController {

	private final BlogService service;
	private final PostViewFactory postViewFactory;

	@Autowired
	public BlogController(BlogService service, PostViewFactory postViewFactory) {
		this.service = service;
		this.postViewFactory = postViewFactory;
	}

	@RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = { GET, HEAD })
	public String showPost(@PathVariable("postId") Long postId, @PathVariable("slug") String slug, Model model) {
		Post post = service.getPublishedPost(postId);
		model.addAttribute("post", postViewFactory.createPostView(post));
		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("activeCategory", post.getCategory().getDisplayName());
		return "blog/show";
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String listPublishedPosts(Model model, @RequestParam(defaultValue = "1") int page, HttpServletRequest request) {
		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = service.getPublishedPosts(pageRequest);
		return renderListOfPosts(result, model, request, "All Posts");
	}

	@RequestMapping(value = "/category/{category}", method = { GET, HEAD })
	public String listPublishedPostsForCategory(@PathVariable("category") PostCategory category, Model model, @RequestParam(defaultValue = "1", value="page") int page, HttpServletRequest request) {
		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = service.getPublishedPosts(category, pageRequest);
		return renderListOfPosts(result, model, request, category.getDisplayName());
	}

	@RequestMapping(value = "/broadcasts", method = { GET, HEAD })
	public String listPublishedBroadcasts(Model model, @RequestParam(defaultValue = "1", value="page") int page, HttpServletRequest request) {
		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = service.getPublishedBroadcastPosts(pageRequest);
		return renderListOfPosts(result, model, request, "Broadcasts");
	}

	private String renderListOfPosts(Page<Post> page, Model model, HttpServletRequest request, String activeCategory) {
		Page<PostView> postViewPage = postViewFactory.createPostViewPage(page);
		List<PostView> posts = postViewPage.getContent();
		model.addAttribute("activeCategory", activeCategory);
		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("posts", posts);
		model.addAttribute("paginationInfo", new PaginationInfo(postViewPage));
		String feedPath = request.getServletPath().replaceAll("/$", "");
		model.addAttribute("feed_path", feedPath + ".atom");
		return "blog/index";
	}
}
