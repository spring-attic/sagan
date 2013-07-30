package org.springframework.site.web.blog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.services.DateService;
import org.springframework.ui.ExtendedModelMap;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

public class BlogControllerTests {

	private PostViewFactory postViewFactory;

	@Mock
	private BlogService blogService;
	private BlogController blogController;

	private ExtendedModelMap model = new ExtendedModelMap();
	private MockHttpServletRequest request = new MockHttpServletRequest();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		postViewFactory = new PostViewFactory(new DateService());
		PageImpl<Post> page = new PageImpl<>(Collections.<Post>emptyList(), new PageRequest(1, 1), 1);
		given(blogService.getPublishedPostsByDate(anyInt(), any(Pageable.class)))
				.willReturn(page);
		given(blogService.getPublishedPostsByDate(anyInt(), anyInt(), any(Pageable.class)))
				.willReturn(page);
		given(blogService.getPublishedPostsByDate(anyInt(), anyInt(), anyInt(), any(Pageable.class)))
				.willReturn(page);

		blogController = new BlogController(blogService, postViewFactory);
	}

	@Test
	public void titleForBlogYearPage() throws Exception {
		blogController.listPublishedPostsForYear(2013, 1, model);
		String title = (String) model.get("title");
		assertThat(title, equalTo("Archive for 2013"));
	}

	@Test
	public void titleForBlogYearMonthPage() throws Exception {
		blogController.listPublishedPostsForYearAndMonth(2013, 1, 1, model);
		String title = (String) model.get("title");
		assertThat(title, equalTo("Archive for January 2013"));
	}

	@Test
	public void titleForBlogYearMonthDayPage() throws Exception {
		blogController.listPublishedPostsForDate(2011, 3, 23, 1, model);
		String title = (String) model.get("title");
		assertThat(title, equalTo("Archive for March 23, 2011"));
	}
}
