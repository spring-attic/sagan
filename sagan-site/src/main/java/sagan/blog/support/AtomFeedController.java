package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles requests for Atom feeds of blog content. All requests return a
 * single {@link Page} of content, the size of which is defined by
 * {@link PageableFactory#forFeeds()}.
 *
 * @see AtomFeedView
 */
@Controller
class AtomFeedController {

    private final BlogService blogService;
    private final AtomFeedView atomFeedView;

    @Autowired
    public AtomFeedController(BlogService blogService, SiteUrl siteUrl, DateFactory dateFactory) {
        this.blogService = blogService;
        this.atomFeedView = new AtomFeedView(siteUrl, dateFactory);
    }

    @RequestMapping(value = "/blog.atom", method = { GET, HEAD })
    public AtomFeedView listPublishedPosts(Model model, HttpServletResponse response) {
        Page<Post> page = blogService.getPublishedPosts(PageableFactory.forFeeds());
        prepareResponse(model, response, page, "", "");
        return atomFeedView;
    }

    @RequestMapping(value = "/blog/category/{category}.atom", method = { GET, HEAD })
    public AtomFeedView listPublishedPostsForCategory(@PathVariable PostCategory category, Model model,
                                                      HttpServletResponse response) {
        Page<Post> page = blogService.getPublishedPosts(category, PageableFactory.forFeeds());
        prepareResponse(model, response, page, category.getDisplayName(), "/category/" + category.getUrlSlug());
        return atomFeedView;
    }

    @RequestMapping(value = "/blog/broadcasts.atom", method = { GET, HEAD })
    public AtomFeedView listPublishedBroadcastPosts(Model model, HttpServletResponse response) {
        Page<Post> page = blogService.getPublishedBroadcastPosts(PageableFactory.forFeeds());
        prepareResponse(model, response, page, "Broadcasts", "/broadcasts");
        return atomFeedView;
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
