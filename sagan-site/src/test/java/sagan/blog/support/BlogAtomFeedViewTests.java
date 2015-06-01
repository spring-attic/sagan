package sagan.blog.support;

import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.support.DateFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import org.springframework.ui.ExtendedModelMap;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BlogAtomFeedViewTests {

    private ExtendedModelMap model = new ExtendedModelMap();
    private SiteUrl siteUrl;
    private AtomFeedView atomFeedView;
    private Feed feed = new Feed();
    private Calendar calendar = Calendar.getInstance(DateFactory.DEFAULT_TIME_ZONE);
    private HttpServletRequest request = mock(HttpServletRequest.class);

    @Before
    public void setUp() throws Exception {
        siteUrl = mock(SiteUrl.class);
        atomFeedView = new AtomFeedView(siteUrl, new DateFactory());
        given(request.getServerName()).willReturn("springsource.org");
        model.addAttribute("posts", new ArrayList<Post>());
    }

    @Test
    public void hasFeedTitleFromModel() {
        model.addAttribute("feed-title", "Spring Engineering");
        atomFeedView.buildFeedMetadata(model, feed, mock(HttpServletRequest.class));
        assertThat(feed.getTitle(), is("Spring Engineering"));
    }

    @Test
    public void hasLinkToAssociatedBlogList() {
        String expectedBlogPath = "/blog/category/engineering";
        String expectedBlogUrl = "http://localhost:8080/blog/category/engineering";
        given(siteUrl.getAbsoluteUrl(eq(expectedBlogPath))).willReturn(expectedBlogUrl);
        model.addAttribute("blog-path", expectedBlogPath);

        atomFeedView.buildFeedMetadata(model, feed, mock(HttpServletRequest.class));

        Link feedLink = (Link) feed.getAlternateLinks().get(0);
        assertThat(feedLink.getHref(), is(expectedBlogUrl));
        assertThat(feedLink.getRel(), is("alternate"));
    }

    @Test
    public void hasLinkToSelf() {
        String expectedFeedPath = "/blog/category/engineering.atom";
        String expectedFeedUrl = "http://localhost:8080/blog/category/engineering.atom";
        given(siteUrl.getAbsoluteUrl(eq(expectedFeedPath))).willReturn(expectedFeedUrl);
        model.addAttribute("feed-path", expectedFeedPath);

        atomFeedView.buildFeedMetadata(model, feed, mock(HttpServletRequest.class));

        Link feedLink = (Link) feed.getOtherLinks().get(0);
        assertThat(feedLink.getHref(), is(expectedFeedUrl));
        assertThat(feedLink.getRel(), is("self"));
    }

    @Test
    public void hasCorrectIdForFeed() throws Exception {
        model.addAttribute("feed-path", "/blog.atom");

        atomFeedView.buildFeedMetadata(model, feed, request);

        assertThat(feed.getId(), is("http://spring.io/blog.atom"));
    }

    @Test
    public void feedUpdatedDateIsMostRecentPublishedPostDate() throws Exception {
        List<Post> posts = new ArrayList<>();
        buildPostsWithDate(5, posts);
        model.addAttribute("posts", posts);

        atomFeedView.buildFeedMetadata(model, feed, request);

        Post latestPost = posts.get(0);
        assertThat(feed.getUpdated(), is(latestPost.getPublishAt()));
    }

    @Test
    public void feedUpdatedDateIsNotPresentWhenThereAreNoPosts() throws Exception {
        List<Post> noPosts = new ArrayList<>();
        model.addAttribute("posts", noPosts);

        atomFeedView.buildFeedMetadata(model, feed, request);

        assertThat(feed.getUpdated(), is(nullValue()));
    }

    private void buildPostsWithDate(int numberOfPosts, List<Post> posts) {
        for (int date = numberOfPosts; date > 0; date--) {
            calendar.set(2013, 6, date);
            Post post = PostBuilder.post().build();
            post.setPublishAt(calendar.getTime());
            posts.add(post);
        }
    }

    @Test
    public void hasCorrectIdForEntry() throws Exception {
        calendar.set(2013, 6, 1);
        Post post = spy(PostBuilder.post().build());
        post.setCreatedAt(calendar.getTime());
        given(post.getId()).willReturn(123L);

        model.addAttribute("posts", Arrays.asList(post));

        List<Entry> entries = atomFeedView.buildFeedEntries(model, request, mock(HttpServletResponse.class));

        Entry entry = entries.get(0);
        assertThat(entry.getId(), is("tag:springsource.org,2013-07-01:123"));
    }
}
