package sagan.site.blog.support;

import sagan.site.blog.Post;
import sagan.site.blog.PostCategory;
import sagan.site.blog.PostMovedException;
import sagan.site.blog.BlogService;
import sagan.support.DateFactory;
import sagan.support.nav.Navigation;
import sagan.support.nav.PageableFactory;
import sagan.support.nav.PaginationInfo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import sagan.support.nav.Section;

/**
 * Controller that handles read-only blog actions, e.g. listing, paging, and showing
 * individual published {@link Post}s. See {@link BlogAdminController} for administrative
 * operations.
 */
@Controller
@RequestMapping("/blog")
@Navigation(Section.BLOG)
class BlogController {

	private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

	private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

	private final String ALL_POSTS_CATEGORY = "All Posts";
	private final String BROADCASTS_CATEGORY = "Broadcasts";

    private final BlogService blogService;
    private final DateFactory dateFactory;

    @Autowired
    public BlogController(BlogService blogService, DateFactory dateFactory) {
        this.blogService = blogService;
        this.dateFactory = dateFactory;
    }

    @GetMapping("/{year:\\d+}/{month:\\d+}/{day:\\d+}/{slug}")
    public String showPost(@PathVariable String year, @PathVariable String month, @PathVariable String day,
                           @PathVariable String slug, Model model) {

        String publicSlug = String.format("%s/%s/%s/%s", year, month, day, slug);
        Post post = blogService.getPublishedPost(publicSlug);
        model.addAttribute("post", PostView.of(post, dateFactory));
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("activeCategory", post.getCategory().getDisplayName());
        model.addAttribute("disqusShortname", blogService.getDisqusShortname());
        return "blog/show";
    }

    @GetMapping("")
    public String listPublishedPosts(Model model, @RequestParam(defaultValue = "1") int page) {
        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = blogService.getPublishedPosts(pageRequest);
        return renderListOfPosts(result, model, ALL_POSTS_CATEGORY);
    }

    @GetMapping("/category/{category}")
    public String listPublishedPostsForCategory(@PathVariable("category") PostCategory category, Model model,
                                                @RequestParam(defaultValue = "1", value = "page") int page) {
        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = blogService.getPublishedPosts(category, pageRequest);
        return renderListOfPosts(result, model, category.getDisplayName());
    }

    @GetMapping("/broadcasts")
    public String listPublishedBroadcasts(Model model, @RequestParam(defaultValue = "1", value = "page") int page) {
        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = blogService.getPublishedBroadcastPosts(pageRequest);
        return renderListOfPosts(result, model, BROADCASTS_CATEGORY);
    }

    @GetMapping("/{year:\\d+}/{month:\\d+}/{day:\\d+}")
    public String listPublishedPostsForDate(@PathVariable int year, @PathVariable int month, @PathVariable int day,
                                            @RequestParam(defaultValue = "1", value = "page") int page, Model model) {

        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = blogService.getPublishedPostsByDate(year, month, day, pageRequest);

		LocalDate date = LocalDate.of(year, month, day);
        model.addAttribute("title", "Archive for " + date.format(DAY_FORMATTER));

        return renderListOfPosts(result, model, ALL_POSTS_CATEGORY);
    }

    @GetMapping("/{year:\\d+}/{month:\\d+}")
    public String listPublishedPostsForYearAndMonth(@PathVariable int year, @PathVariable int month,
                                                    @RequestParam(defaultValue = "1", value = "page") int page,
                                                    Model model) {

        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = blogService.getPublishedPostsByDate(year, month, pageRequest);
		YearMonth yearMonth = YearMonth.of(year, month);
        model.addAttribute("title", "Archive for " + yearMonth.format(MONTH_FORMATTER));
        return renderListOfPosts(result, model, ALL_POSTS_CATEGORY);
    }

    @GetMapping("/{year:\\d+}")
    public String listPublishedPostsForYear(@PathVariable int year,
                                            @RequestParam(defaultValue = "1", value = "page") int page, Model model) {

        Pageable pageRequest = PageableFactory.forLists(page);
        Page<Post> result = blogService.getPublishedPostsByDate(year, pageRequest);
        model.addAttribute("title", String.format("Archive for %d", year));
        return renderListOfPosts(result, model, ALL_POSTS_CATEGORY);
    }

    @ExceptionHandler
    public RedirectView handle(PostMovedException moved) {
        RedirectView redirect = new RedirectView();
        redirect.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirect.setUrl("/blog/" + moved.getPublicSlug());
        return redirect;
    }

    private String renderListOfPosts(Page<Post> page, Model model, String activeCategory) {
        Page<PostView> postViewPage = PostView.pageOf(page, dateFactory);
        List<PostView> posts = postViewPage.getContent();
        if (page.isFirst() && ALL_POSTS_CATEGORY.equals(activeCategory)) {
			List<PostView> recentPosts = new ArrayList<>(posts);
			model.addAttribute("newestPost", recentPosts.remove(0));
			model.addAttribute("posts", recentPosts);
		}
        else {
        	model.addAttribute("posts", posts);
		}

        model.addAttribute("activeCategory", activeCategory);
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("paginationInfo", new PaginationInfo(postViewPage));
        model.addAttribute("disqusShortname", blogService.getDisqusShortname());
        return "blog/index";
    }
}
