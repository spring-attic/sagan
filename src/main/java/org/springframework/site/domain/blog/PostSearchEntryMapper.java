package org.springframework.site.domain.blog;

import org.jsoup.Jsoup;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

public class PostSearchEntryMapper implements SearchEntryMapper<Post> {

	@Override
	public SearchEntry map(Post post) {
		SearchEntry entry = new SearchEntry();
		entry.setTitle(post.getTitle());

		String summary = Jsoup.parse(post.getRenderedSummary()).text();
		String content = Jsoup.parse(post.getRenderedContent()).text();

		entry.setSummary(summary);
		entry.setRawContent(content);
		//TODO this should be encapsulated somewhere else
		entry.setPath("/blog/" + post.getSlug());
		entry.setPublishAt(post.getPublishAt());
		return entry;
	}

}
