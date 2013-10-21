package sagan.blog.web;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.blog.service.CachedBlogService;
import sagan.blog.view.PostView;
import sagan.blog.view.PostViewFactory;
import sagan.util.web.NavSection;
import sagan.util.web.PageableFactory;
import sagan.util.web.PaginationInfo;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/blog")
@NavSection("blog")
public class BlogController {

    private final CachedBlogService service;
    private final PostViewFactory postViewFactory;

    @Autowired
    public BlogController(CachedBlogService service, PostViewFactory postViewFactory) {
        this.service = service;
        this.postViewFactory = postViewFactory;
    }

    @RequestMapping(value = "/{year:\\d+}/{month:\\d+}/{day:\\d+}/{slug}", method = { GET, HEAD })
    public String showPost(@PathVariable String year, @PathVariable String month, @PathVariable String day,
                           @PathVariable String slug, Model model) {

        String publicSlug = String.format("%s/%s/%s/%s", year, month, day, slug);
        Post post = this.service.getPublishedPost(publicSlug);
        model.addAttribute("post", this.postViewFactory.createPostView(post));
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("activeCategory", post.getCategory().getDisplayName());
        return "blog/show";
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String listPublishedPosts(Model model, @RequestParam(defaultValue = "1") int page) {
        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = this.service.getPublishedPosts(pageRequest);
        return renderListOfPosts(result, model, "All Posts");
    }

    @RequestMapping(value = "/category/{category}", method = { GET, HEAD })
    public String listPublishedPostsForCategory(@PathVariable("category") PostCategory category, Model model,
                                                @RequestParam(defaultValue = "1", value = "page") int page) {
        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = this.service.getPublishedPosts(category, pageRequest);
        return renderListOfPosts(result, model, category.getDisplayName());
    }

    @RequestMapping(value = "/broadcasts", method = { GET, HEAD })
    public String listPublishedBroadcasts(Model model, @RequestParam(defaultValue = "1", value = "page") int page) {
        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = this.service.getPublishedBroadcastPosts(pageRequest);
        return renderListOfPosts(result, model, "Broadcasts");
    }

    @RequestMapping(value = "/{year:\\d+}/{month:\\d+}/{day:\\d+}", method = { GET, HEAD })
    public String listPublishedPostsForDate(@PathVariable int year, @PathVariable int month, @PathVariable int day,
                                            @RequestParam(defaultValue = "1", value = "page") int page, Model model) {

        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = this.service.getPublishedPostsByDate(year, month, day, pageRequest);

        LocalDate date = new LocalDate(year, month, day);
        model.addAttribute("title", "Archive for " + date.toString("MMMM dd, yyyy"));

        return renderListOfPosts(result, model, "All Posts");
    }

    @RequestMapping(value = "/{year:\\d+}/{month:\\d+}", method = { GET, HEAD })
    public String listPublishedPostsForYearAndMonth(@PathVariable int year, @PathVariable int month,
                                                    @RequestParam(defaultValue = "1", value = "page") int page,
                                                    Model model) {

        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = this.service.getPublishedPostsByDate(year, month, pageRequest);
        YearMonth yearMonth = new YearMonth(year, month);
        model.addAttribute("title", "Archive for " + yearMonth.toString("MMMM yyyy"));
        return renderListOfPosts(result, model, "All Posts");
    }

    @RequestMapping(value = "/{year:\\d+}", method = { GET, HEAD })
    public String listPublishedPostsForYear(@PathVariable int year,
                                            @RequestParam(defaultValue = "1", value = "page") int page, Model model) {

        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = this.service.getPublishedPostsByDate(year, pageRequest);
        model.addAttribute("title", String.format("Archive for %d", year));
        return renderListOfPosts(result, model, "All Posts");
    }

    private String renderListOfPosts(Page<Post> page, Model model, String activeCategory) {
        Page<PostView> postViewPage = this.postViewFactory.createPostViewPage(page);
        List<PostView> posts = postViewPage.getContent();
        model.addAttribute("activeCategory", activeCategory);
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("posts", posts);
        model.addAttribute("paginationInfo", new PaginationInfo(postViewPage));
        return "blog/index";
    }
}
