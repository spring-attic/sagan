package sagan.site.learn;

import java.util.ArrayList;
import java.util.List;

import sagan.blog.Post;
import sagan.blog.support.PostView;
import sagan.site.blog.BlogService;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LearnController {

	private final BlogService blogService;

	private final DateFactory dateFactory;

	public LearnController(BlogService blogService, DateFactory dateFactory) {
		this.blogService = blogService;
		this.dateFactory = dateFactory;
	}

	@GetMapping("/learn")
	public String learn(Model model) {
		Page<Post> page = this.blogService.getPublishedPosts(PageableFactory.first(4));
		Page<PostView> postViewPage = PostView.pageOf(page, dateFactory);
		List<PostView> posts = new ArrayList<>(postViewPage.getContent());
		model.addAttribute("newestPost", posts.remove(0));
		model.addAttribute("posts", posts);
		return "learn/index";
	}
}
