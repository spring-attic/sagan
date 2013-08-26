package io.spring.site.web.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.spring.site.domain.blog.BlogService;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostCategory;
import io.spring.site.domain.blog.PostForm;
import io.spring.site.domain.blog.PostSearchEntryMapper;
import io.spring.site.domain.team.GeoLocation;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.TeamRepository;
import io.spring.site.search.SearchService;

import java.security.Principal;
import java.util.Date;

@Controller
@RequestMapping("/admin/sample")
public class BlogSampleDataController {

	@Autowired
	private BlogAdminController blogAdminController;

	@Autowired
	private BlogService blogService;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private SearchService searchService;

	private PostSearchEntryMapper mapper = new PostSearchEntryMapper();

	@RequestMapping(method = RequestMethod.GET)
	public String showSampleIndex(Model model, Principal principal) throws Exception {
		return "admin/sample/index";
	}


	@RequestMapping(value = "/posts", method = RequestMethod.POST)
	public String createSamplePosts(Model model, Principal principal) throws Exception {
		for (int i = 0; i < 8; i++) {
			generateRandomBlogPost(principal.getName());
		}
		return "redirect:/admin/blog";
	}

	@RequestMapping(value = "/teammembers", method = RequestMethod.POST)
	public String createSampleTeamMembers(Model model, Principal principal) throws Exception {
		for (int i = 0; i < 8; i++) {
			generateRandomTeamMember(i);
		}
		return "redirect:/team";
	}

	private static String[] locations = {
			"Greece",
			"Cairo",
			"Turkmenistan",
			"Austria",
			"Melbourne",
			"Gabon",
			"San Francisco",
			"New York"
	};

	private static GeoLocation[] geoLocations = {
			new GeoLocation(39.04453f, 22.98798f),
			new GeoLocation(30.0505f, 31.2499f),
			new GeoLocation(39.12222f, 59.38353f),
			new GeoLocation(9.96713f, -84.06132f),
			new GeoLocation(28.08363f, -80.60811f),
			new GeoLocation(-0.59068f, 11.79716f),
			new GeoLocation(37.77896f, -122.4192f),
			new GeoLocation(40.7306f, -73.98658f)
	};

	private void generateRandomTeamMember(int index) {
		MemberProfile profile = new MemberProfile();
		profile.setUsername("member_" + index);
		profile.setName("Member " + index);
		profile.setLocation(locations[index % locations.length]);
		profile.setGeoLocation(geoLocations[index % geoLocations.length]);
		profile.setAvatarUrl("http://lorempixel.com/80/80/people/" + index);
		teamRepository.save(profile);
	}

	private void generateRandomBlogPost(String authorMemberName) {
		PostCategory[] categories = PostCategory.values();
		long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;
		PostForm postForm = new PostForm();
		postForm.setCategory(categories[(int) (Math.random() * categories.length)]);
		Date date = new Date(System.currentTimeMillis() - (long) ((Math.random() * 30 - 15) * MILLISECONDS_PER_DAY));
		postForm.setPublishAt(date);
		if (Math.random() > 0.7) {
			postForm.setDraft(true);
		}
		postForm.setTitle(String.format("This week in Spring - %1$te/%1$tm/%1$tY", date));
		postForm.setContent("Welcome to another installment of This Week in **Spring**. The SpringOne2GX super early bird registration discount expires on June 10th, 2013, so make your arrangements now to secure the discount. Also, we've got three webinars coming up this month, check out the details below. As usual, we've got a lot to cover, so let's get to it!\n" +
				"\n" +
				"1.  I'll be doing a webinar on building effective REST APIs with Spring on June 13th. I'll be introducing Spring's deep support for REST services, starting");
		Post post = blogService.addPost(postForm, authorMemberName);
		if (post.isLiveOn(new Date())) {
			try {
				searchService.saveToIndex(mapper.map(post));
			} catch (Exception e) {
			}
		}
	}
}
