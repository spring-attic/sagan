package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;
import sagan.team.MemberProfile;
import sagan.team.support.TeamRepository;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import static org.springframework.web.bind.annotation.RequestMethod.*;

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

    @RequestMapping(value = "", method = { GET, HEAD })
    public String dashboard(Model model) {
        Pageable pageRequest = PageableFactory.forDashboard();
        model.addAttribute("posts", PostView.pageOf(service.getPublishedPosts(pageRequest), dateFactory));
        model.addAttribute("drafts", PostView.pageOf(service.getDraftPosts(pageRequest), dateFactory));
        model.addAttribute("scheduled", PostView.pageOf(service.getScheduledPosts(pageRequest), dateFactory));
        return "admin/blog/index";
    }

    @RequestMapping(value = "/new", method = { GET, HEAD })
    public String newPost(Model model) {
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("categories", PostCategory.values());
        return "admin/blog/new";
    }

    @RequestMapping(value = "/{postId:[0-9]+}{slug:.*}/edit", method = { GET, HEAD })
    public String editPost(@PathVariable Long postId, @PathVariable String slug, Model model) {
        Post post = service.getPost(postId);
        PostForm postForm = new PostForm(post);

        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("postForm", postForm);
        model.addAttribute("post", post);
        return "admin/blog/edit";
    }

    @RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = { GET, HEAD })
    public String showPost(@PathVariable Long postId, @PathVariable String slug, Model model) {
        model.addAttribute("post", PostView.of(service.getPost(postId), dateFactory));
        return "admin/blog/show";
    }

    @RequestMapping(value = "", method = { POST })
    public String createPost(Principal principal, @Valid PostForm postForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", PostCategory.values());
            return "admin/blog/new";
        } else {
            MemberProfile memberProfile = teamRepository.findById(new Long(principal.getName()));
            try {
                Post post = service.addPost(postForm, memberProfile.getUsername());
                PostView postView = PostView.of(post, dateFactory);
                return "redirect:" + postView.getPath();
            } catch (DataIntegrityViolationException ex) {
                model.addAttribute("categories", PostCategory.values());
                model.addAttribute("postForm", postForm);
                bindingResult.rejectValue("title", "duplicate_post",
                        "A blog post with this publication date and title already exists");
                return "admin/blog/new";
            }
        }
    }

    @RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = PUT)
    public String updatePost(@PathVariable Long postId, @Valid PostForm postForm, BindingResult bindingResult,
                             Model model) {
        Post post = service.getPost(postId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", PostCategory.values());
            model.addAttribute("post", post);
            return "admin/blog/edit";
        } else {
            try {
                service.updatePost(post, postForm);
                PostView postView = PostView.of(post, dateFactory);
                return "redirect:" + postView.getPath();
            } catch (RestClientException e) {
                model.addAttribute("categories", PostCategory.values());
                model.addAttribute("post", post);
                model.addAttribute("githubBroken", true);
                return "admin/blog/edit";
            }
        }
    }

    @RequestMapping(value = "/{postId:[0-9]+}{slug:.*}", method = DELETE)
    public String deletePost(@PathVariable Long postId) {
        Post post = service.getPost(postId);
        service.deletePost(post);
        return "redirect:/admin/blog";
    }

    @RequestMapping(value = "resummarize", method = POST)
    public String resummarizeAllBlogPosts() {
        service.resummarizeAllPosts();
        return "redirect:/admin/blog";
    }

}
