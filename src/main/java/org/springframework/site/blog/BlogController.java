package org.springframework.site.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/blog")
public class BlogController {

	private BlogService service;

	@Autowired
	public BlogController(BlogService service) {
		this.service = service;
	}

	@RequestMapping(value = "/{postId:[0-9]+}", method = { GET, HEAD })
	public String showPost(@PathVariable Long postId, Model model) {
		model.addAttribute("post", service.getPost(postId));
		return "blog/show";
	}

}
