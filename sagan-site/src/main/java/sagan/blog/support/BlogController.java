package sagan.blog.support;

import sagan.blog.PostMovedException;
import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.support.nav.NavSection;
import sagan.support.nav.PageableFactory;
import sagan.support.nav.PaginationInfo;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles read-only blog actions, e.g. listing, paging, and showing
 * individual published {@link Post}s. See {@link BlogAdminController} for administrative
 * operations.
 */
@Controller
@RequestMapping("/blog")
@NavSection("blog")
class BlogController {

    private final BlogService service;
    private final PostViewFactory postViewFactory;

    @Autowired
    public BlogController(BlogService service, PostViewFactory postViewFactory) {
        this.service = service;
        this.postViewFactory = postViewFactory;
    }

    @RequestMapping(value = "/{year:\\d+}/{month:\\d+}/{day:\\d+}/{slug}", method = { GET, HEAD })
    public String showPost(@PathVariable String year, @PathVariable String month, @PathVariable String day,
                           @PathVariable String slug, Model model) {

        String publicSlug = String.format("%s/%s/%s/%s", year, month, day, slug);
        Post post = service.getPublishedPost(publicSlug);
        model.addAttribute("post", postViewFactory.createPostView(post));
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("activeCategory", post.getCategory().getDisplayName());
        model.addAttribute("disqusShortname", service.getDisqusShortname());
        return "blog/show";
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String listPublishedPosts(Model model, @RequestParam(defaultValue = "1") int page) {
        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = service.getPublishedPosts(pageRequest);
        return renderListOfPosts(result, model, "All Posts");
    }

    @RequestMapping(value = "/category/{category}", method = { GET, HEAD })
    public String listPublishedPostsForCategory(@PathVariable("category") PostCategory category, Model model,
                                                @RequestParam(defaultValue = "1", value = "page") int page) {
        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = service.getPublishedPosts(category, pageRequest);
        return renderListOfPosts(result, model, category.getDisplayName());
    }

    @RequestMapping(value = "/broadcasts", method = { GET, HEAD })
    public String listPublishedBroadcasts(Model model, @RequestParam(defaultValue = "1", value = "page") int page) {
        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = service.getPublishedBroadcastPosts(pageRequest);
        return renderListOfPosts(result, model, "Broadcasts");
    }

    @RequestMapping(value = "/{year:\\d+}/{month:\\d+}/{day:\\d+}", method = { GET, HEAD })
    public String listPublishedPostsForDate(@PathVariable int year, @PathVariable int month, @PathVariable int day,
                                            @RequestParam(defaultValue = "1", value = "page") int page, Model model) {

        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = service.getPublishedPostsByDate(year, month, day, pageRequest);

        LocalDate date = new LocalDate(year, month, day);
        model.addAttribute("title", "Archive for " + date.toString("MMMM dd, yyyy"));

        return renderListOfPosts(result, model, "All Posts");
    }

    @RequestMapping(value = "/{year:\\d+}/{month:\\d+}", method = { GET, HEAD })
    public String listPublishedPostsForYearAndMonth(@PathVariable int year, @PathVariable int month,
                                                    @RequestParam(defaultValue = "1", value = "page") int page,
                                                    Model model) {

        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = service.getPublishedPostsByDate(year, month, pageRequest);
        YearMonth yearMonth = new YearMonth(year, month);
        model.addAttribute("title", "Archive for " + yearMonth.toString("MMMM yyyy"));
        return renderListOfPosts(result, model, "All Posts");
    }

    @RequestMapping(value = "/{year:\\d+}", method = { GET, HEAD })
    public String listPublishedPostsForYear(@PathVariable int year,
                                            @RequestParam(defaultValue = "1", value = "page") int page, Model model) {

        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = service.getPublishedPostsByDate(year, pageRequest);
        model.addAttribute("title", String.format("Archive for %d", year));
        return renderListOfPosts(result, model, "All Posts");
    }

    @ExceptionHandler
    public RedirectView handle(PostMovedException moved) {
        RedirectView redirect = new RedirectView();
        redirect.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirect.setUrl("/blog/" + moved.getPublicSlug());
        return redirect;
    }

    private String renderListOfPosts(Page<Post> page, Model model, String activeCategory) {
        Page<PostView> postViewPage = postViewFactory.createPostViewPage(page);
        List<PostView> posts = postViewPage.getContent();
        model.addAttribute("activeCategory", activeCategory);
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("posts", posts);
        model.addAttribute("paginationInfo", new PaginationInfo(postViewPage));
        model.addAttribute("disqusShortname", service.getDisqusShortname());
        return "blog/index";
    }
}
