package org.springframework.site.blog.feed;

import com.sun.syndication.feed.atom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.blog.Post;
import org.springframework.site.services.SiteUrl;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlogPostAtomViewer extends AbstractAtomFeedView {

	private final SiteUrl siteUrl;

	@Autowired
	public BlogPostAtomViewer(SiteUrl siteUrl) {
		this.siteUrl = siteUrl;
	}

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
		feed.setTitle((String) model.get("feed-title"));
		String postPath = (String) model.get("feed-path");
		String postUrl = siteUrl.getAbsoluteUrl(postPath);
		Link postLink = new Link();
		postLink.setHref(postUrl);
		feed.setAlternateLinks(Arrays.asList(postLink));
		feed.setIcon(siteUrl.getAbsoluteUrl("/favicon.ico"));
		super.buildFeedMetadata(model, feed, request);
	}

	@Override
	protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Post> posts = (List<Post>) model.get("posts");
		List<Entry> entries = new ArrayList<Entry>(posts.size());

		for (Post post : posts) {
			Entry entry = new Entry();
			entry.setTitle(post.getTitle());
			entry.setUpdated(post.getCreatedDate());
			setRenderedContent(post, entry);
			setPostUrl(post, entry);
			setAuthor(entry);
			setCategories(post, entry);
			entries.add(entry);
		}

		return entries;
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		super.render(model, request, response);
	}

	private void setRenderedContent(Post post, Entry entry) {
		Content content = new Content();
		content.setType(Content.HTML);
		content.setValue(post.getRenderedContent());
		entry.setContents(Arrays.asList(content));
	}

	private void setPostUrl(Post post, Entry entry) {
		String postUrl = siteUrl.getAbsoluteUrl(post.getPath());
		Link postLink = new Link();
		postLink.setHref(postUrl);
		entry.setAlternateLinks(Arrays.asList(postLink));
	}

	private void setAuthor(Entry entry) {
		Person person = new Person();
		person.setName("Spring Team");
		entry.setAuthors(Arrays.asList(person));
	}

	private void setCategories(Post post, Entry entry) {
		Category category = new Category();
		category.setLabel(post.getCategory().getDisplayName());
		List<Category> categories = new ArrayList<Category>();
		categories.add(category);

		if (post.isBroadcast()) {
			Category broadcast = new Category();
			broadcast.setLabel("Broadcast");
			categories.add(broadcast);
		}
		entry.setCategories(categories);
	}
}