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
import org.springframework.web.util.UrlPathHelper;

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
	private final UrlPathHelper urlPathHelper = new UrlPathHelper();

	@Autowired
	public BlogController(BlogService service, PostViewFactory postViewFactory) {
		this.service = service;
		this.postViewFactory = postViewFactory;
	}

	@RequestMapping(value = "/{year:\\d+}/{month:\\d+}/{day:\\d+}/{slug}", method = { GET, HEAD })
	public String showPost(@PathVariable String year, @PathVariable String month, @PathVariable String day, @PathVariable String slug, Model model) {

		Post post = service.getPublishedPost(String.format("%s/%s/%s/%s", year, month, day, slug));
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


	@RequestMapping(value = "/{year:\\d+}/{month:\\d+}/{day:\\d+}", method = { GET, HEAD })
	public String listPublishedPostsForDate(@PathVariable int year,
											@PathVariable int month,
											@PathVariable int day,
											@RequestParam(defaultValue = "1", value="page") int page,
											Model model, HttpServletRequest request) {

		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = service.getPublishedPostsByDate(year, month, day, pageRequest);
		return renderListOfPosts(result, model, request, "All Posts");
	}

	@RequestMapping(value = "/{year:\\d+}/{month:\\d+}", method = { GET, HEAD })
	public String listPublishedPostsForYearAndMonth(@PathVariable int year,
													@PathVariable int month,
													@RequestParam(defaultValue = "1", value = "page") int page,
													Model model, HttpServletRequest request) {

		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = service.getPublishedPostsByDate(year, month, pageRequest);
		return renderListOfPosts(result, model, request, "All Posts");
	}

	@RequestMapping(value = "/{year:\\d+}", method = { GET, HEAD })
	public String listPublishedPostsForYear(@PathVariable int year,
											@RequestParam(defaultValue = "1", value = "page") int page,
											Model model, HttpServletRequest request) {

		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = service.getPublishedPostsByDate(year, pageRequest);
		return renderListOfPosts(result, model, request, "All Posts");
	}

	private String renderListOfPosts(Page<Post> page, Model model, HttpServletRequest request, String activeCategory) {
		Page<PostView> postViewPage = postViewFactory.createPostViewPage(page);
		List<PostView> posts = postViewPage.getContent();
		model.addAttribute("activeCategory", activeCategory);
		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("posts", posts);
		model.addAttribute("paginationInfo", new PaginationInfo(postViewPage));
		String path = urlPathHelper.getPathWithinServletMapping(request);
		model.addAttribute("pagePath", path);
		model.addAttribute("feedPath", path + ".atom");
		return "blog/index";
	}
}
