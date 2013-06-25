package org.springframework.site.admin.blog;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.site.blog.BlogService;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.blog.admin.BlogAdminController;
import org.springframework.ui.ExtendedModelMap;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BlogAdminControllerTests {
	private BlogAdminController controller;

	@Mock
	private BlogService blogService;

	@Before
	public void setup() {
		controller = new BlogAdminController(blogService);
	}

	@Test
	public void dashboardShowsUsersPosts() {
		List<Post> posts = Collections.singletonList(PostBuilder.post().build());
		when(blogService.mostRecentPosts(any(PageRequest.class))).thenReturn(posts);
		ExtendedModelMap model = new ExtendedModelMap();
		controller.dashboard(model);
		assertThat((List< Post >)model.get("myPosts"), equalTo(posts));
	}
}
