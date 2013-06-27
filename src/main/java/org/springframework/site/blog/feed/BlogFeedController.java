package org.springframework.site.blog.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostCategory;
import org.springframework.site.blog.web.BlogPostsPageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
		List<Post> posts = service.getPublishedPosts(BlogPostsPageRequest.forFeeds()).getItems();
		return renderBlogFeeds(model,posts, "", "");
	}

	@RequestMapping(value = "/blog/category/{category}.atom", method = { GET, HEAD })
	public String listPublishedPostsForCategory(@PathVariable PostCategory category, Model model) {
		List<Post> posts = service.getPublishedPosts(category, BlogPostsPageRequest.forFeeds()).getItems();
		return renderBlogFeeds(model, posts, category.getDisplayName(), "/category/" + category.getUrlSlug());
	}

	@RequestMapping(value = "/blog/broadcasts.atom", method = { GET, HEAD })
	public String listPublishedBroadcastPosts(Model model) {
		List<Post> posts = service.getPublishedBroadcastPosts(BlogPostsPageRequest.forFeeds()).getItems();
		return renderBlogFeeds(model, posts, "Broadcasts", "/broadcasts");
	}

	private String renderBlogFeeds(Model model, List<Post> posts, String category, String subPath) {
		model.addAttribute("posts", posts);
		model.addAttribute("feed-title", ("Spring " + category).trim());
		model.addAttribute("feed-path", "/blog" + subPath);
		return "blogPostAtomViewer";
	}
}
