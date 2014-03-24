package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;
import sagan.support.nav.SiteUrl;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
class BlogAtomFeedController {

    private final BlogService blogService;
    private final BlogAtomFeedView blogAtomFeedView;

    @Autowired
    public BlogAtomFeedController(BlogService blogService, SiteUrl siteUrl, DateFactory dateFactory) {
        this.blogService = blogService;
        this.blogAtomFeedView = new BlogAtomFeedView(siteUrl, dateFactory);
    }

    @RequestMapping(value = "/blog.atom", method = { GET, HEAD })
    public BlogAtomFeedView listPublishedPosts(Model model, HttpServletResponse response) {
        Page<Post> page = blogService.getPublishedPosts(PageableFactory.forFeeds());
        prepareResponse(model, response, page, "", "");
        return blogAtomFeedView;
    }

    @RequestMapping(value = "/blog/category/{category}.atom", method = { GET, HEAD })
    public BlogAtomFeedView listPublishedPostsForCategory(@PathVariable PostCategory category, Model model,
                                                          HttpServletResponse response) {
        Page<Post> page = blogService.getPublishedPosts(category, PageableFactory.forFeeds());
        prepareResponse(model, response, page, category.getDisplayName(), "/category/" + category.getUrlSlug());
        return blogAtomFeedView;
    }

    @RequestMapping(value = "/blog/broadcasts.atom", method = { GET, HEAD })
    public BlogAtomFeedView listPublishedBroadcastPosts(Model model, HttpServletResponse response) {
        Page<Post> page = blogService.getPublishedBroadcastPosts(PageableFactory.forFeeds());
        prepareResponse(model, response, page, "Broadcasts", "/broadcasts");
        return blogAtomFeedView;
    }

    private void prepareResponse(Model model, HttpServletResponse response, Page<Post> page,
                                 String category, String subPath) {
        response.setCharacterEncoding("utf-8");
        model.addAttribute("posts", page.getContent());
        model.addAttribute("feed-title", ("Spring " + category).trim());
        String blogPath = "/blog" + subPath;
        model.addAttribute("blog-path", blogPath);
        model.addAttribute("feed-path", blogPath + ".atom");
    }
}
