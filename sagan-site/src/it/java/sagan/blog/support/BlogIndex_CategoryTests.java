package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import saganx.AbstractIntegrationTests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class BlogIndex_CategoryTests extends AbstractIntegrationTests {
    @Autowired
    private PostRepository postRepository;
    private String path = "/blog/category/" + PostCategory.ENGINEERING.getUrlSlug();

    private void createManyPostsInNovember(int numPostsToCreate) {
        Calendar calendar = Calendar.getInstance();
        List<Post> posts = new ArrayList<>();
        for (int postNumber = 1; postNumber <= numPostsToCreate; postNumber++) {
            calendar.set(2012, 10, postNumber);
            Post post = new PostBuilder().title("This week in Spring - November " + postNumber + ", 2012")
                    .rawContent("Raw content")
                    .renderedContent("Html content")
                    .renderedSummary("Html summary")
                    .category(PostCategory.ENGINEERING)
                    .createdAt(calendar.getTime())
                    .publishAt(calendar.getTime())
                    .build();
            posts.add(post);
        }
        postRepository.save(posts);
    }

    @Test
    public void viewBlogPostsForCategory() throws Exception {
        postRepository.save(PostBuilder.post()
                .title("DO NOT LOOK AT ME")
                .category(PostCategory.RELEASES).build());

        postRepository.save(PostBuilder.post()
                .title("An Engineering Post")
                .category(PostCategory.ENGINEERING).build());

        Page<Post> posts = postRepository.findByCategoryAndDraftFalse(PostCategory.ENGINEERING, new PageRequest(0, 10));
        MatcherAssert.assertThat(posts.getSize(), greaterThanOrEqualTo(1));

        MvcResult response = mockMvc.perform(get(path))
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("An Engineering Post")))
                .andExpect(content().string(not(containsString("DO NOT LOOK AT ME"))))
                .andExpect(content().string(containsString(PostCategory.ENGINEERING.toString())))
                .andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());

        assertThat(html.select(".secondary-nav .blog-category.active").text(), equalTo(PostCategory.ENGINEERING
                .getDisplayName()));

        assertThat(html.select(".content--title.blog-category.active").text(), equalTo(PostCategory.ENGINEERING
                .getDisplayName()));

        assertThat(html.head().getElementsByAttributeValue("type", "application/atom+xml").get(0).attr("href"),
                equalTo("/blog/category/engineering.atom"));
    }

    @Test
    public void given1PageOfResults_blogIndexDoesNotShowPaginationControl() throws Exception {
        createManyPostsInNovember(1);

        MvcResult response = mockMvc.perform(get(path)).andReturn();
        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(html.select("#pagination_control").first(), is(nullValue()));
    }

    @Test
    public void givenManyPosts_blogIndexShowsPaginationControl() throws Exception {
        createManyPostsInNovember(21);

        MvcResult response = mockMvc.perform(get(path + "?page=2")).andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());

        Element previousLink = html.select("#pagination_control a.previous").first();
        assertThat("No previous pagination link found", previousLink, is(notNullValue()));
        String previousHref = previousLink.attributes().get("href");
        assertThat(previousHref, is(path + "?page=1"));

        Element nextLink = html.select("#pagination_control a.next").first();
        assertThat("No next pagination link found", nextLink, is(notNullValue()));
        String nextHref = nextLink.attributes().get("href");
        assertThat(nextHref, is(path + "?page=3"));
    }

}
