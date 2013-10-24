package sagan.blog.web.feed;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.blog.service.BlogService;
import sagan.util.web.PageableFactory;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
public class BlogFeedController {

    private BlogService service;

    @Autowired
    public BlogFeedController(BlogService service) {
        this.service = service;
    }

    @RequestMapping(value = "/blog.atom", method = { GET, HEAD })
    public String listPublishedPosts(Model model, HttpServletResponse response) {
        Page<Post> page = service.getPublishedPosts(PageableFactory.forFeeds());
        return renderBlogFeeds(model, page, "", "", response);
    }

    @RequestMapping(value = "/blog/category/{category}.atom", method = { GET, HEAD })
    public String listPublishedPostsForCategory(@PathVariable PostCategory category, Model model,
                                                HttpServletResponse response) {
        Page<Post> page = service.getPublishedPosts(category, PageableFactory.forFeeds());
        return renderBlogFeeds(model, page, category.getDisplayName(), "/category/" + category.getUrlSlug(), response);
    }

    @RequestMapping(value = "/blog/broadcasts.atom", method = { GET, HEAD })
    public String listPublishedBroadcastPosts(Model model, HttpServletResponse response) {
        Page<Post> page = service.getPublishedBroadcastPosts(PageableFactory.forFeeds());
        return renderBlogFeeds(model, page, "Broadcasts", "/broadcasts", response);
    }

    private String renderBlogFeeds(Model model, Page<Post> page, String category, String subPath,
                                   HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        model.addAttribute("posts", page.getContent());
        model.addAttribute("feed-title", ("Spring " + category).trim());
        String blogPath = "/blog" + subPath;
        model.addAttribute("blog-path", blogPath);
        model.addAttribute("feed-path", blogPath + ".atom");
        return "blogPostAtomViewer";
    }
}
