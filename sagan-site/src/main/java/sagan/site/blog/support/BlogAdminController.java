package sagan.site.blog.support;

import java.security.Principal;
import java.util.Collections;

import javax.validation.Valid;

import sagan.site.blog.BlogService;
import sagan.site.blog.Post;
import sagan.site.blog.PostCategory;
import sagan.site.blog.PostForm;
import sagan.site.blog.PostFormat;
import sagan.site.team.MemberProfile;
import sagan.site.team.support.TeamRepository;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;
import sagan.support.nav.PaginationInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller that handles administrative blog actions, e.g. creating, editing and
 * deleting posts. Per rules in {@code sagan.SecurityConfig}, authentication is required
 * for all requests. See {@link BlogController} for public, read-only operations.
 */
@Controller
@RequestMapping("/admin/blog")
class BlogAdminController {

    private BlogService service;
    private TeamRepository teamRepository;
    private DateFactory dateFactory;

    @Autowired
    public BlogAdminController(BlogService service, TeamRepository teamRepository, DateFactory dateFactory) {
        this.service = service;
        this.teamRepository = teamRepository;
        this.dateFactory = dateFactory;
    }

    @GetMapping("")
    public String dashboard(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<PostView> postViewPage = PostView.pageOf(service.getPublishedPosts(PageableFactory.forDashboard(page)), dateFactory);
        model.addAttribute("posts", postViewPage);
        model.addAttribute("paginationInfo", new PaginationInfo(postViewPage));

        if(page == 1) {
            model.addAttribute("drafts", PostView.pageOf(service.getDraftPosts(PageableFactory.all()), dateFactory));
            model.addAttribute("scheduled", PostView.pageOf(service.getScheduledPosts(PageableFactory.all()), dateFactory));
        } else {
            Page<PostView> emptyPage = new PageImpl<PostView>(Collections.emptyList(), PageableFactory.all(), 0);
            model.addAttribute("drafts", emptyPage);
            model.addAttribute("scheduled", emptyPage);
        }

        return "admin/blog/index";
    }

    @GetMapping("/new")
    public String newPost(Model model) {
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("formats", PostFormat.values());
        return "admin/blog/new";
    }

    @GetMapping("/{postId:[0-9]+}{slug:.*}/edit")
    public String editPost(@PathVariable Long postId, @PathVariable String slug, Model model) {
        Post post = service.getPost(postId);
        PostForm postForm = new PostForm(post);
        String path = PostView.of(post, dateFactory).getPath();

        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("formats", PostFormat.values());
        model.addAttribute("postForm", postForm);
        model.addAttribute("post", post);
        model.addAttribute("path", path);
        return "admin/blog/edit";
    }

    @GetMapping("/{postId:[0-9]+}{slug:.*}")
    public String showPost(@PathVariable Long postId, @PathVariable String slug, Model model) {
        model.addAttribute("post", PostView.of(service.getPost(postId), dateFactory));
        return "blog/show";
    }

    @PostMapping("")
    public String createPost(Principal principal, @Valid PostForm postForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", PostCategory.values());
            model.addAttribute("formats", PostFormat.values());
            return "admin/blog/new";
        } else {
            MemberProfile memberProfile = teamRepository.findById(new Long(principal.getName())).get();
            try {
                Post post = service.addPost(postForm, memberProfile.getUsername());
                PostView postView = PostView.of(post, dateFactory);
                return "redirect:" + postView.getPath() + "/edit";
            } catch (DataIntegrityViolationException ex) {
                model.addAttribute("categories", PostCategory.values());
                model.addAttribute("postForm", postForm);
                bindingResult.rejectValue("title", "duplicate_post",
                        "A blog post with this publication date and title already exists");
                return "admin/blog/new";
            }
        }
    }

    @PostMapping("/{postId:[0-9]+}{slug:.*}/edit")
    public String updatePost(@PathVariable Long postId, @Valid PostForm postForm, BindingResult bindingResult,
                             Model model) {
        Post post = service.getPost(postId);
        if (!bindingResult.hasErrors()) {
            service.updatePost(post, postForm);
        }
        PostView postView = PostView.of(post, dateFactory);
        String path = postView.getPath();

        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("formats", PostFormat.values());
        model.addAttribute("post", post);
        model.addAttribute("path", path);
        return "/admin/blog/edit";
    }

    @PostMapping("/{postId:[0-9]+}{slug:.*}/delete")
    public String deletePost(@PathVariable Long postId) {
        Post post = service.getPost(postId);
        service.deletePost(post);
        return "redirect:/admin/blog";
    }

    @PostMapping("resummarize")
    public String resummarizeAllBlogPosts() {
        service.resummarizeAllPosts();
        return "redirect:/admin/blog";
    }

    @PostMapping("refreshblogposts")
    @ResponseBody
    public String refreshBlogPosts(
            @RequestParam(value="page", defaultValue = "1", required = false) int page,
            @RequestParam(value="size", defaultValue = "10", required = false) int size) {
        Page<Post> posts = service.refreshPosts(page, size);
        return String.format("{page: %s, pageSize: %s, totalPages: %s, totalElements: %s}",
                posts.getNumber(), posts.getSize(), posts.getTotalPages(), posts.getTotalElements());
    }

}
