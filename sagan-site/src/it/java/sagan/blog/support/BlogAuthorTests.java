package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.team.MemberProfile;
import sagan.team.MemberProfileBuilder;
import sagan.team.support.TeamRepository;
import saganx.AbstractIntegrationTests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class BlogAuthorTests extends AbstractIntegrationTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    public void blogIndexPostsIncludeLinkToAuthor() throws Exception {
        MemberProfile activeAuthor = MemberProfileBuilder.profile().username("active_author").build();
        teamRepository.save(activeAuthor);

        Post post = new PostBuilder().title("Blog Post ").author(activeAuthor).build();
        postRepository.save(post);

        MvcResult response = mockMvc.perform(get("/blog")).andReturn();
        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(html.select("a.author").first().attr("href"), containsString(activeAuthor.getUsername()));
    }

    @Test
    public void blogIndexPostsDoNotIncludeLinksToHiddenAuthors() throws Exception {
        MemberProfile activeAuthor =
                MemberProfileBuilder.profile().name("Hidden Author").username("hidden_author").hidden(true).build();
        teamRepository.save(activeAuthor);

        Post post = new PostBuilder().title("Blog Post ").author(activeAuthor).build();
        postRepository.save(post);

        MvcResult response = mockMvc.perform(get("/blog")).andReturn();
        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertTrue(html.select("a.author").isEmpty());
        assertThat(html.text(), containsString("Hidden Author"));
    }

    @Test
    public void blogPostPageIncludesLinkToAuthor() throws Exception {
        MemberProfile activeAuthor = MemberProfileBuilder.profile().username("active_author").build();
        teamRepository.save(activeAuthor);

        Post post = new PostBuilder().title("Blog Post ").author(activeAuthor).build();
        postRepository.save(post);

        MvcResult response = mockMvc.perform(get("/blog/" + post.getPublicSlug())).andReturn();
        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(html.select("a.author").first().attr("href"), containsString(activeAuthor.getUsername()));
    }

    @Test
    public void blogPostPageDoesNotIncludeLinkToHiddenAuthors() throws Exception {
        MemberProfile activeAuthor =
                MemberProfileBuilder.profile().name("Hidden Author").username("hidden_author").hidden(true).build();
        teamRepository.save(activeAuthor);

        Post post = new PostBuilder().title("Blog Post ").author(activeAuthor).build();
        postRepository.save(post);

        MvcResult response = mockMvc.perform(get("/blog/" + post.getPublicSlug())).andReturn();
        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertTrue(html.select("a.author").isEmpty());
        assertThat(html.text(), containsString("Hidden Author"));
    }

}
