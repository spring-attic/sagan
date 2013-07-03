package integration.blog;

import integration.configuration.ElasticsearchStubConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.blog.PostCategory;
import org.springframework.site.blog.PostRepository;
import org.springframework.site.services.SiteUrl;
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
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
@ContextConfiguration(classes = ElasticsearchStubConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
public class BlogAtomFeedsTests {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	SiteUrl siteUrl;

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

	private Document getAtomFeedDocument(MvcResult mvcResult) throws ParserConfigurationException, SAXException, IOException {
		String atomFeed = mvcResult.getResponse().getContentAsString();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(atomFeed.getBytes()));
	}

	private Document doGetForDocument(String path) throws Exception {
		ResultActions resultActions = mockMvc.perform(get(path));
		MvcResult mvcResult = resultActions.andReturn();
		return getAtomFeedDocument(mvcResult);
	}

	@Test
	public void feedHasCorrectMetadata() throws Exception {
		Document doc = doGetForDocument("/blog.atom");

		assertThat(xpath.evaluate("/feed/title", doc), is("Spring"));
		assertThat(xpath.evaluate("/feed/icon", doc), is(siteUrl.getAbsoluteUrl("/favicon.ico")));
		assertThat(xpath.evaluate("/feed/link/@href", doc), is(siteUrl.getAbsoluteUrl("/blog")));
	}

	@Test
	public void rendersBroadcastsFeed() throws Exception {
		Document doc = doGetForDocument("/blog/broadcasts.atom");

		assertThat(xpath.evaluate("/feed/title", doc), is("Spring Broadcasts"));
		assertThat(xpath.evaluate("/feed/link/@href", doc), is(siteUrl.getAbsoluteUrl("/blog/broadcasts")));
	}

	@Test
	public void rendersCategoryFeed() throws Exception {
		Document doc = doGetForDocument("/blog/category/news.atom");

		assertThat(xpath.evaluate("/feed/title", doc), is("Spring News and Events"));
		assertThat(xpath.evaluate("/feed/link/@href", doc), is(siteUrl.getAbsoluteUrl("/blog/category/news")));
	}

	@Test
	public void containsBlogPostFields() throws Exception {
		Post post = PostBuilder.post().category(PostCategory.ENGINEERING).isBroadcast().build();
		postRepository.save(post);

		ResultActions resultActions = mockMvc.perform(get("/blog.atom"));
		MvcResult mvcResult = resultActions
				.andExpect(status().isOk())
				.andReturn();

		String atomFeed = mvcResult.getResponse().getContentAsString();
		assertThat(atomFeed, containsString(post.getTitle()));
		assertThat(atomFeed, containsString(post.getRenderedContent()));

		String postDate = new SimpleDateFormat("yyyy-MM-dd").format(post.getCreatedAt());
		assertThat(atomFeed, containsString(postDate));
		assertThat(atomFeed, containsString("/blog/" + post.getSlug()));
		assertThat(atomFeed, containsString(PostCategory.ENGINEERING.getDisplayName()));
		assertThat(atomFeed, containsString("Broadcast"));
	}

	@Test
	public void containsAMaximumOf20Posts() throws Exception {
		createPosts(21);

		Document doc = doGetForDocument("/blog.atom");

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
