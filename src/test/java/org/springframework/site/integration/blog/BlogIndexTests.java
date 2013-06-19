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
		Post post = new PostBuilder().title("This week in Spring - June 3, 2013")
									 .rawContent("Raw content")
									 .renderedContent("Html content")
									 .build();
		postRepository.save(post);

		MvcResult response = this.mockMvc.perform(get("/blog"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.select("ul.posts li h2").first().text(), is(post.getTitle()));
	}

}