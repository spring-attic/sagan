package org.springframework.site.blog.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostCategory;
import org.springframework.site.web.PageableFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
public class BlogFeedController {

	private BlogService service;

	@Autowired
	public BlogFeedController(BlogService service) {
		this.service = service;
	}

	@RequestMapping(value="/blog.atom", method = { GET, HEAD })
	public String listPublishedPosts(Model model) {
		Page<Post> page = service.getPublishedPosts(PageableFactory.forFeeds());
		return renderBlogFeeds(model, page, "", "");
	}

	@RequestMapping(value = "/blog/category/{category}.atom", method = { GET, HEAD })
	public String listPublishedPostsForCategory(@PathVariable PostCategory category, Model model) {
		Page<Post> page = service.getPublishedPosts(category, PageableFactory.forFeeds());
		return renderBlogFeeds(model, page, category.getDisplayName(), "/category/" + category.getUrlSlug());
	}

	@RequestMapping(value = "/blog/broadcasts.atom", method = { GET, HEAD })
	public String listPublishedBroadcastPosts(Model model) {
		Page<Post> page = service.getPublishedBroadcastPosts(PageableFactory.forFeeds());
		return renderBlogFeeds(model, page, "Broadcasts", "/broadcasts");
	}

	private String renderBlogFeeds(Model model, Page<Post> page, String category, String subPath) {
		model.addAttribute("posts", page.getContent());
		model.addAttribute("feed-title", ("Spring " + category).trim());
		String blogPath = "/blog" + subPath;
		model.addAttribute("blog-path", blogPath);
		model.addAttribute("feed-path", blogPath + ".atom");
		return "blogPostAtomViewer";
	}
}
