package integration.blog;

import integration.IntegrationTestBase;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostBuilder;
import io.spring.site.domain.blog.PostRepository;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.MemberProfileBuilder;
import io.spring.site.domain.team.TeamRepository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class BlogAuthorTests extends IntegrationTestBase {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private TeamRepository teamRepository;


	@Test
	public void blogIndexPostsIncludeLinkToAuthor() throws Exception {
		MemberProfile activeAuthor = MemberProfileBuilder.profile().username("active_author").build();
		teamRepository.save(activeAuthor);

		Post post = new PostBuilder().title("Blog Post ")
				.author(activeAuthor)
				.build();
		postRepository.save(post);

		MvcResult response = this.mockMvc.perform(get("/blog")).andReturn();
		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.select("a.author").first().attr("href"), containsString(activeAuthor.getUsername()));
	}

	@Test
	public void blogIndexPostsDoNotIncludeLinksToHiddenAuthors() throws Exception {
		MemberProfile activeAuthor = MemberProfileBuilder.profile()
				.name("Hidden Author")
				.username("hidden_author")
				.hidden(true)
				.build();
		teamRepository.save(activeAuthor);

		Post post = new PostBuilder().title("Blog Post ")
				.author(activeAuthor)
				.build();
		postRepository.save(post);

		MvcResult response = this.mockMvc.perform(get("/blog")).andReturn();
		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertTrue(html.select("a.author").isEmpty());
		assertThat(html.text(), containsString("Hidden Author"));
	}

	@Test
	public void blogPostPageIncludesLinkToAuthor() throws Exception {
		MemberProfile activeAuthor = MemberProfileBuilder.profile()
				.username("active_author")
				.build();
		teamRepository.save(activeAuthor);

		Post post = new PostBuilder().title("Blog Post ")
				.author(activeAuthor)
				.build();
		postRepository.save(post);

		MvcResult response = this.mockMvc.perform(get("/blog/" + post.getPublicSlug())).andReturn();
		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertThat(html.select("a.author").first().attr("href"), containsString(activeAuthor.getUsername()));
	}

	@Test
	public void blogPostPageDoesNotIncludeLinkToHiddenAuthors() throws Exception {
		MemberProfile activeAuthor = MemberProfileBuilder.profile()
				.name("Hidden Author")
				.username("hidden_author")
				.hidden(true)
				.build();
		teamRepository.save(activeAuthor);

		Post post = new PostBuilder().title("Blog Post ")
				.author(activeAuthor)
				.build();
		postRepository.save(post);

		MvcResult response = this.mockMvc.perform(get("/blog/" + post.getPublicSlug())).andReturn();
		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		assertTrue(html.select("a.author").isEmpty());
		assertThat(html.text(), containsString("Hidden Author"));
	}

}