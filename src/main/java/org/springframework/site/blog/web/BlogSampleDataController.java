package org.springframework.site.blog.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.PostCategory;
import org.springframework.site.blog.PostForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/admin/blog/sample")
public class BlogSampleDataController {

	@Autowired
	BlogAdminController blogAdminController;

	@Autowired
	BlogService blogService;

	@RequestMapping
	public String createSamples(Model model) throws Exception {
		for (int i = 0; i < 8; i++) {
			generateRandomBlogPost();
		}
		return blogAdminController.dashboard(model);
	}

	private void generateRandomBlogPost() {
		PostCategory[] categories = PostCategory.values();
		String[] authors = new String[] {"nickstreet", "kseebaldt", "dsyer"};
		long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;
		PostForm postForm = new PostForm();
		postForm.setCategory(categories[(int) (Math.random() * categories.length)]);
		Date date = new Date(System.currentTimeMillis() - (long) ((Math.random() * 30 - 15) * MILLISECONDS_PER_DAY));
		postForm.setPublishAt(date);
		if (Math.random() > 0.5) {
			postForm.setDraft(true);
		}
		postForm.setTitle(String.format("This week in Spring - %1$te/%1$tm/%1$tY", date));
		postForm.setContent("Welcome to another installment of This Week in **Spring**. The SpringOne2GX super early bird registration discount expires on June 10th, 2013, so make your arrangements now to secure the discount. Also, we've got three webinars coming up this month, check out the details below. As usual, we've got a lot to cover, so let's get to it!\n" +
				"\n" +
				"1.  I'll be doing a webinar on building effective REST APIs with Spring on June 13th. I'll be introducing Spring's deep support for REST services, starting");
		blogService.addPost(postForm, authors[(int)(Math.random()*authors.length)]);
	}
}
