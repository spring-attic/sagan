package org.springframework.site.integration.blog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.blog.PostRepository;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
public class BlogAtomFeedsTests {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private PostRepository postRepository;

	private MockMvc mockMvc;

	private XPath xpath = XPathFactory.newInstance().newXPath();

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
	public void containsBlogPostFields() throws Exception {
		Post post = PostBuilder.post().isBroadcast().build();
		postRepository.save(post);

		ResultActions resultActions = mockMvc.perform(get("/blog/categories/blog.atom"));
		MvcResult mvcResult = resultActions
				.andExpect(status().isOk())
				.andReturn();

		String atomFeed = mvcResult.getResponse().getContentAsString();
		assertThat(atomFeed, containsString(post.getTitle()));
		assertThat(atomFeed, containsString(post.getRenderedContent()));

		String postDate = new SimpleDateFormat("yyyy-MM-dd").format(post.getCreatedDate());
		assertThat(atomFeed, containsString(postDate));

		assertThat(atomFeed, containsString(post.getPath()));
	}

	@Test
	public void containsAMaximumOf20Posts() throws Exception {
		createPosts(21);

		String urlTemplate = "/blog/categories/blog.atom";
		MvcResult mvcResult = mockMvc.perform(get(urlTemplate)).andReturn();
		String atomFeed = mvcResult.getResponse().getContentAsString();
		atomFeed = atomFeed.replaceAll("\r", "");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(atomFeed.getBytes()));

		XPathExpression expression = xpath.compile("//entry");
		NodeList evaluate = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
		assertThat(evaluate.getLength(), is(20));
	}

	private void createPosts(int numPostsToCreate) {
		Calendar calendar = Calendar.getInstance();
		List<Post> posts = new ArrayList<Post>();
		for (int postNumber = 1; postNumber <= numPostsToCreate; postNumber++) {
			calendar.set(2013, 10, postNumber);
			Post post = new PostBuilder().title("This week in Spring - November " + postNumber + ", 2013")
					.rawContent("Raw content")
					.renderedContent("Html content")
					.renderedSummary("Html summary")
					.dateCreated(calendar.getTime())
					.build();
			posts.add(post);
		}
		postRepository.save(posts);
	}
}
