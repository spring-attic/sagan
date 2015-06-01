package sagan.blog.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
import sagan.blog.Post;
import sagan.support.DateFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;


/**
 * Spring MVC {@code View} responsible for rendering Atom feeds for the blog and each of
 * its categories.
 * 
 * @see AtomFeedController
 */
class AtomFeedView extends AbstractAtomFeedView {

    private final SiteUrl siteUrl;
    private final DateFactory dateFactory;

    @Autowired
    public AtomFeedView(SiteUrl siteUrl, DateFactory dateFactory) {
        this.siteUrl = siteUrl;
        this.dateFactory = dateFactory;
    }

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        String feedPath = (String) model.get("feed-path");
        feed.setTitle((String) model.get("feed-title"));
        feed.setId(String.format("http://spring.io%s", feedPath));
        feed.setIcon(siteUrl.getAbsoluteUrl("/favicon.ico"));
        setFeedUrl(feedPath, feed);
        setBlogUrl((String) model.get("blog-path"), feed);
        setUpdatedDate(model, feed);

        super.buildFeedMetadata(model, feed, request);
    }

    private void setUpdatedDate(Map<String, Object> model, Feed feed) {
        @SuppressWarnings("unchecked")
        List<Post> posts = (List<Post>) model.get("posts");
        if (posts.size() > 0) {
            Post latestPost = posts.get(0);
            feed.setUpdated(latestPost.getPublishAt());
        }
    }

    private void setFeedUrl(String feedPath, Feed feed) {
        String feedUrl = siteUrl.getAbsoluteUrl(feedPath);
        Link feedLink = new Link();
        feedLink.setHref(feedUrl);
        feedLink.setRel("self");
        feed.setOtherLinks(Arrays.asList(feedLink));
    }

    private void setBlogUrl(String blogPath, Feed feed) {
        String blogUrl = siteUrl.getAbsoluteUrl(blogPath);
        Link blogLink = new Link();
        blogLink.setHref(blogUrl);
        feed.setAlternateLinks(Arrays.asList(blogLink));
    }

    @Override
    protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        @SuppressWarnings("unchecked")
        List<Post> posts = (List<Post>) model.get("posts");
        List<Entry> entries = new ArrayList<>(posts.size());

        for (Post post : posts) {
            Entry entry = new Entry();
            entry.setTitle(post.getTitle());
            setId(post, entry, request);
            entry.setUpdated(post.getPublishAt());
            setRenderedContent(post, entry);
            setPostUrl(post, entry);
            setAuthor(post, entry);
            setCategories(post, entry);
            entries.add(entry);
        }

        return entries;
    }

    private void setId(Post post, Entry entry, HttpServletRequest request) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(dateFactory.timeZone());
        String dateString = dateFormat.format(post.getCreatedAt());
        String host = request.getServerName();
        String id = String.format("tag:%s,%s:%s", host, dateString, post.getId());
        entry.setId(id);
    }

    private void setRenderedContent(Post post, Entry entry) {
        Content content = new Content();
        content.setType(Content.HTML);
        content.setValue(post.getRenderedContent());
        entry.setContents(Arrays.asList(content));
    }

    private void setPostUrl(Post post, Entry entry) {
        PostView postView = PostView.of(post, dateFactory);
        String postUrl = siteUrl.getAbsoluteUrl(postView.getPath());
        Link postLink = new Link();
        postLink.setHref(postUrl);
        entry.setAlternateLinks(Arrays.asList(postLink));
    }

    private void setAuthor(Post post, Entry entry) {
        Person person = new Person();
        person.setName(post.getAuthor().getName());
        entry.setAuthors(Arrays.asList(person));
    }

    private void setCategories(Post post, Entry entry) {
        Category category = new Category();
        category.setLabel(post.getCategory().getDisplayName());
        category.setTerm(post.getCategory().getUrlSlug());
        List<Category> categories = new ArrayList<>();
        categories.add(category);

        if (post.isBroadcast()) {
            Category broadcast = new Category();
            broadcast.setLabel("Broadcast");
            broadcast.setTerm("broadcast");
            categories.add(broadcast);
        }
        entry.setCategories(categories);
    }
}
