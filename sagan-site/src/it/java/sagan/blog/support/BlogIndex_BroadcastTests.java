package sagan.blog.support;

import sagan.blog.Post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Ignore;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import sagan.blog.PostBuilder;
import saganx.AbstractIntegrationTests;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class BlogIndex_BroadcastTests extends AbstractIntegrationTests {
    @Autowired
    private PostRepository postRepository;

    private int numberOfBlogPosts(Document html) {
        return html.select(".blog--title").size();
    }

    private void createManyPostsInNovember(int numPostsToCreate) {
        Calendar calendar = Calendar.getInstance();
        List<Post> posts = new ArrayList<Post>();
        for (int postNumber = 1; postNumber <= numPostsToCreate; postNumber++) {
            calendar.set(2012, 10, postNumber);
            Post post = new PostBuilder().title("This week in Spring - November " + postNumber + ", 2012")
                    .rawContent("Raw content")
                    .renderedContent("Html content")
                    .renderedSummary("Html summary")
                    .isBroadcast()
                    .createdAt(calendar.getTime())
                    .publishAt(calendar.getTime())
                    .build();
            posts.add(post);
        }
        postRepository.save(posts);
    }

    @Test
    @Ignore
    // the broadcast category has been removed for the time being
    public void viewBroadcastBlogPosts() throws Exception {
        createManyPostsInNovember(2);

        postRepository.save(PostBuilder.post()
                .title("A non-broadcast post")
                .build());

        postRepository.save(PostBuilder.post()
                .title("Another non-broadcast post")
                .build());

        postRepository.save(PostBuilder.post()
                .title("Yet another")
                .build());

        MvcResult response = mockMvc.perform(get("/blog/broadcasts")).andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());

        assertThat(html.select(".blog-category.active").text(), equalTo("Broadcasts"));

        assertThat(numberOfBlogPosts(html), is(2));

        assertThat(html.head().getElementsByAttributeValue("type", "application/atom+xml").get(0).attr("href"),
                equalTo("/blog/broadcasts.atom"));
    }

    @Test
    public void given1PageOfResults_blogIndexDoesNotShowPaginationControl() throws Exception {
        createManyPostsInNovember(1);

        MvcResult response = mockMvc.perform(get("/blog/broadcasts")).andReturn();
        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(html.select("#pagination_control").first(), is(nullValue()));
    }

    @Test
    public void givenManyPosts_blogIndexShowsPaginationControl() throws Exception {
        createManyPostsInNovember(21);

        MvcResult response = mockMvc.perform(get("/blog/broadcasts?page=2")).andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());

        Element previousLink = html.select("#pagination_control a.previous").first();
        assertThat("No previous pagination link found", previousLink, is(notNullValue()));
        String previousHref = previousLink.attributes().get("href");
        assertThat(previousHref, is("/blog/broadcasts?page=1"));

        Element nextLink = html.select("#pagination_control a.next").first();
        assertThat("No next pagination link found", nextLink, is(notNullValue()));
        String nextHref = nextLink.attributes().get("href");
        assertThat(nextHref, is("/blog/broadcasts?page=3"));
    }

}
