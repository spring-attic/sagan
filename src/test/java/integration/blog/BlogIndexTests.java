package integration.blog;

import integration.IntegrationTestBase;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostBuilder;
import io.spring.site.domain.blog.PostRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BlogIndexTests extends IntegrationTestBase {

	@Autowired
	private PostRepository postRepository;

	@Test
	public void showsBlogIndex() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/blog")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html")).andReturn();

		Document document = Jsoup.parse(result.getResponse().getContentAsString());
		assertThat(document.select("ul.nav li.active").text(), equalTo("Blog"));

		assertThat(document.select(".blog-category.active").text(), equalTo("All Posts"));
		assertThat(
				document.head()
						.getElementsByAttributeValue("type", "application/atom+xml")
						.get(0).attr("href"), equalTo("/blog.atom"));
	}

	@Test
	public void given1Post_blogIndexShowsPostSummary() throws Exception {
		Post post = createSinglePost();

		MvcResult response = this.mockMvc.perform(get("/blog"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html")).andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.select(".blog--title a").first().text(), is(post.getTitle()));
	}

	@Test
	public void givenPostContentThatIsEqualToSummary_blogIndexShouldNotShow_ReadMore()
			throws Exception {
		String summary = "A blog post string that is longish.";
		String content = summary;
		Post post = new PostBuilder().renderedContent(content).renderedSummary(summary)
				.build();

		this.postRepository.save(post);

		MvcResult result = this.mockMvc.perform(get("/blog")).andReturn();
		String response = result.getResponse().getContentAsString();
		assertThat(response, not(containsString("Read more")));
	}

	@Test
	public void givenPostContentThatIsLongerThanSummary_blogIndexShouldShow_ReadMore()
			throws Exception {
		String summary = "A blog post string that is longish.";
		String content = "";
		for (int i = 0; i < 50; i++) {
			content = content + summary;
		}
		Post post = new PostBuilder().renderedContent(content).renderedSummary(summary)
				.build();

		this.postRepository.save(post);

		MvcResult result = this.mockMvc.perform(get("/blog")).andReturn();
		String response = result.getResponse().getContentAsString();
		assertThat(response, containsString("Read more"));
	}

	private Post createSinglePost() {
		Post post = new PostBuilder().title("This week in Spring - June 3, 2013")
				.rawContent("Raw content").renderedContent("Html content")
				.renderedSummary("Html summary").build();
		this.postRepository.save(post);
		return post;
	}

	@Test
	public void givenManyPosts_blogIndexShowsLatest10PostSummaries() throws Exception {
		createManyPostsInNovember(11);

		MvcResult response = this.mockMvc.perform(get("/blog"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html")).andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());

		assertThat(numberOfBlogPosts(html), is(10));
		assertThat(html.select(".blog--container .date").first().text(),
				is("November 11, 2012"));
		assertThat(html.select(".blog--container .date").last().text(),
				is("November 2, 2012"));
	}

	private int numberOfBlogPosts(Document html) {
		return html.select(".blog--title").size();
	}

	private void createManyPostsInNovember(int numPostsToCreate) {
		Calendar calendar = Calendar.getInstance();
		List<Post> posts = new ArrayList<Post>();
		for (int postNumber = 1; postNumber <= numPostsToCreate; postNumber++) {
			calendar.set(2012, 10, postNumber);
			Post post = new PostBuilder()
					.title("This week in Spring - November " + postNumber + ", 2012")
					.rawContent("Raw content").renderedContent("Html content")
					.renderedSummary("Html summary").createdAt(calendar.getTime())
					.publishAt(calendar.getTime()).build();
			posts.add(post);
		}
		this.postRepository.save(posts);
	}

	@Test
	public void given1PageOfResults_blogIndexDoesNotShowPaginationControl()
			throws Exception {
		createManyPostsInNovember(1);

		MvcResult response = this.mockMvc.perform(get("/blog")).andReturn();
		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.select("#pagination_control").first(), is(nullValue()));
	}

	@Test
	public void givenManyPosts_blogIndexShowsPaginationControl() throws Exception {
		createManyPostsInNovember(21);

		MvcResult response = this.mockMvc.perform(get("/blog?page=2")).andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());

		Element previousLink = html.select("#pagination_control a.previous").first();
		assertThat("No previous pagination link found", previousLink, is(notNullValue()));
		String previousHref = previousLink.attributes().get("href");
		assertThat(previousHref, is("/blog?page=1"));

		Element nextLink = html.select("#pagination_control a.next").first();
		assertThat("No next pagination link found", nextLink, is(notNullValue()));
		String nextHref = nextLink.attributes().get("href");
		assertThat(nextHref, is("/blog?page=3"));
	}
}