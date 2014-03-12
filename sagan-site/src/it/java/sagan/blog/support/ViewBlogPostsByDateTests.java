package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import saganx.AbstractIntegrationTests;

import java.text.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ViewBlogPostsByDateTests extends AbstractIntegrationTests {
    @Autowired
    private PostRepository postRepository;

    private Post post1, post2, post3;

    @Before
    public void setup() throws ParseException {
        post1 = PostBuilder.post()
                .publishAt("2013-04-01 11:00")
                .title("Title 1")
                .rawContent("Content")
                .category(PostCategory.ENGINEERING).build();
        postRepository.save(post1);

        post2 = PostBuilder.post()
                .publishAt("2013-04-02 11:00")
                .title("Title 2")
                .rawContent("Content")
                .category(PostCategory.ENGINEERING).build();
        postRepository.save(post2);

        post3 = PostBuilder.post()
                .publishAt("2013-03-02 11:00")
                .title("Title 3")
                .rawContent("Content")
                .category(PostCategory.ENGINEERING).build();
        postRepository.save(post3);

        Post oldPost = PostBuilder.post()
                .publishAt("2012-04-01 11:00")
                .title("Old Post")
                .rawContent("Content")
                .category(PostCategory.ENGINEERING).build();
        postRepository.save(oldPost);

    }

    @Test
    public void getDateReturnsPostsForDate() throws Exception {
        mockMvc.perform(get("/blog/2013/04/01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Title 1")))
                .andExpect(content().string(not(containsString("Title 2"))))
                .andExpect(content().string(not(containsString("Title 3"))))
                .andExpect(content().string(not(containsString("Old Post"))));
    }

    @Test
    public void getMonthReturnsPostsForMonth() throws Exception {
        mockMvc.perform(get("/blog/2013/04"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Title 1")))
                .andExpect(content().string(containsString("Title 2")))
                .andExpect(content().string(not(containsString("Title 3"))))
                .andExpect(content().string(not(containsString("Old Post"))));
    }

    @Test
    public void getMonthReturnsPostsForYear() throws Exception {
        mockMvc.perform(get("/blog/2013"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Title 1")))
                .andExpect(content().string(containsString("Title 2")))
                .andExpect(content().string(containsString("Title 3")))
                .andExpect(content().string(not(containsString("Old Post"))));
    }

    @Test
    public void givenManyPosts_blogYearMonthDayIndexShowsPaginationControl() throws Exception {
        for (int i = 0; i < 24; ++i) {
            Post post = PostBuilder.post()
                    .publishAt(String.format("2012-11-02 %02d:00", i))
                    .title(String.format("Title %d", i))
                    .rawContent("Content")
                    .category(PostCategory.ENGINEERING).build();
            postRepository.save(post);
        }

        MvcResult response = mockMvc.perform(get("/blog/2012/11/02?page=2")).andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());

        Element previousLink = html.select("#pagination_control a.previous").first();
        assertThat("No previous pagination link found", previousLink, is(notNullValue()));
        String previousHref = previousLink.attributes().get("href");
        assertThat(previousHref, is("/blog/2012/11/02?page=1"));

        Element nextLink = html.select("#pagination_control a.next").first();
        assertThat("No next pagination link found", nextLink, is(notNullValue()));
        String nextHref = nextLink.attributes().get("href");
        assertThat(nextHref, is("/blog/2012/11/02?page=3"));
    }

    @Test
    public void givenManyPosts_blogYearMonthIndexShowsPaginationControl() throws Exception {
        for (int i = 1; i < 25; ++i) {
            Post post = PostBuilder.post()
                    .publishAt(String.format("2012-06-%02d 11:00", i))
                    .title(String.format("Title %d", i))
                    .rawContent("Content")
                    .category(PostCategory.ENGINEERING).build();
            postRepository.save(post);
        }

        MvcResult response = mockMvc.perform(get("/blog/2012/06?page=2")).andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());

        Element previousLink = html.select("#pagination_control a.previous").first();
        assertThat("No previous pagination link found", previousLink, is(notNullValue()));
        String previousHref = previousLink.attributes().get("href");
        assertThat(previousHref, is("/blog/2012/06?page=1"));

        Element nextLink = html.select("#pagination_control a.next").first();
        assertThat("No next pagination link found", nextLink, is(notNullValue()));
        String nextHref = nextLink.attributes().get("href");
        assertThat(nextHref, is("/blog/2012/06?page=3"));
    }

    @Test
    public void givenManyPosts_blogYearIndexShowsPaginationControl() throws Exception {
        for (int i = 1; i < 25; ++i) {
            Post post = PostBuilder.post()
                    .publishAt(String.format("2012-11-%02d 11:00", i))
                    .title(String.format("Title %d", i))
                    .rawContent("Content")
                    .category(PostCategory.ENGINEERING).build();
            postRepository.save(post);
        }

        MvcResult response = mockMvc.perform(get("/blog/2012?page=2")).andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());

        Element previousLink = html.select("#pagination_control a.previous").first();
        assertThat("No previous pagination link found", previousLink, is(notNullValue()));
        String previousHref = previousLink.attributes().get("href");
        assertThat(previousHref, is("/blog/2012?page=1"));

        Element nextLink = html.select("#pagination_control a.next").first();
        assertThat("No next pagination link found", nextLink, is(notNullValue()));
        String nextHref = nextLink.attributes().get("href");
        assertThat(nextHref, is("/blog/2012?page=3"));
    }

}
