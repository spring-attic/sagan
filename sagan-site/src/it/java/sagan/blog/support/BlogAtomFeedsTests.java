package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;
import saganx.AbstractIntegrationTests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BlogAtomFeedsTests extends AbstractIntegrationTests {

    @Autowired
    SiteUrl siteUrl;

    @Autowired
    private PostRepository postRepository;

    private XPath xpath = XPathFactory.newInstance().newXPath();

    private Document getAtomFeedDocument(MvcResult mvcResult) throws ParserConfigurationException, SAXException,
            IOException {
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
                .andExpect(content().contentTypeCompatibleWith("application/atom+xml"))
                .andReturn();

        assertThat(mvcResult.getResponse().getCharacterEncoding(), equalTo("utf-8"));

        String atomFeed = mvcResult.getResponse().getContentAsString();
        assertThat(atomFeed, containsString(post.getTitle()));
        assertThat(atomFeed, containsString(post.getRenderedContent()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(DateFactory.DEFAULT_TIME_ZONE);
        String postDate = dateFormat.format(post.getCreatedAt());
        assertThat(atomFeed, containsString(postDate));
        assertThat(atomFeed, containsString("/blog/" + post.getPublicSlug()));
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
        List<Post> posts = new ArrayList<>();
        for (int postNumber = 1; postNumber <= numPostsToCreate; postNumber++) {
            calendar.set(2013, 10, postNumber);
            Post post = new PostBuilder().title("This week in Spring - November " + postNumber + ", 2013")
                    .rawContent("Raw content")
                    .renderedContent("Html content")
                    .renderedSummary("Html summary")
                    .createdAt(calendar.getTime())
                    .build();
            posts.add(post);
        }
        postRepository.save(posts);
    }
}
