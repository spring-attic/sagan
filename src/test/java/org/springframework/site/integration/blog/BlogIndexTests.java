package org.springframework.site.integration.blog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.repository.PostRepository;
import org.springframework.site.test.configuration.OfflineApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = OfflineApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
public class BlogIndexTests {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private PostRepository postRepository;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		postRepository.deleteAll();
	}

	@After
	public void tearDown() throws Exception {
		postRepository.deleteAll();
	}

	@Test
	public void given1Post_blogIndexShowsPostSummary() throws Exception {
		Post post = createSinglePost();

		MvcResult response = mockMvc.perform(get("/blog"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.select("ul.posts li h2").first().text(), is(post.getTitle()));
	}

	private Post createSinglePost() {
		Post post = new PostBuilder().title("This week in Spring - June 3, 2013")
										 .rawContent("Raw content")
										 .renderedContent("Html content")
										 .build();
		postRepository.save(post);
		return post;
	}

	@Test
	public void givenManyPosts_blogIndexShowsLatest10PostSummaries() throws Exception {
		createManyPostsInNovember(11);

		MvcResult response = mockMvc.perform(get("/blog"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.select("ul.posts li").size(), is(10));
		assertThat(html.select("ul.posts li .date").first().text(), is("November 11, 2013"));
		assertThat(html.select("ul.posts li .date").last().text(), is("November 2, 2013"));
	}

	private void createManyPostsInNovember(int numPostsToCreate) {
		Calendar calendar = Calendar.getInstance();
		List<Post> posts = new ArrayList<Post>();
		for (int postNumber = 1; postNumber <= numPostsToCreate; postNumber++) {
			calendar.set(2013, 10, postNumber);
			Post post = new PostBuilder().title("This week in Spring - November " + postNumber + ", 2013")
					.rawContent("Raw content")
					.renderedContent("Html content")
					.dateCreated(calendar.getTime())
					.build();
			posts.add(post);
		}
		postRepository.save(posts);
	}

	@Test
	public void givenRequestForInvalidPage_blogIndexReturns404() throws Exception {
		createSinglePost();

		mockMvc.perform(get("/blog?page=2"))
				.andExpect(status().isNotFound())
				.andReturn();
	}
}