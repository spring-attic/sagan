package org.springframework.site.domain.blog;

import org.jsoup.Jsoup;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

public class PostSearchEntryMapper implements SearchEntryMapper<Post> {

	@Override
	public SearchEntry map(Post post) {
		SearchEntry entry = new SearchEntry();
		entry.setTitle(post.getTitle());
		entry.setSubTitle("Blog Post");

		String summary = Jsoup.parse(post.getRenderedSummary()).text();
		String content = Jsoup.parse(post.getRenderedContent()).text();

		entry.setSummary(summary);
		entry.setRawContent(content);
		entry.addFacetPaths("Blog", "Blog/" + post.getCategory().getDisplayName());
		//TODO this should be encapsulated somewhere else
		entry.setPath("/blog/" + post.getPublicSlug());
		entry.setPublishAt(post.getPublishAt());
		return entry;
	}

}
