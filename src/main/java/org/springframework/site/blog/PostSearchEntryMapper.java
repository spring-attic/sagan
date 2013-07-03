package org.springframework.site.blog;

import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

public class PostSearchEntryMapper implements SearchEntryMapper<Post> {

	@Override
	public SearchEntry map(Post post) {
		SearchEntry entry = new SearchEntry();
		entry.setId(post.getId().toString());
		entry.setTitle(post.getTitle());
		entry.setSummary(post.getRenderedSummary());
		entry.setRawContent(post.getRawContent());
		//TODO this should be encapsulated somewhere else
		entry.setPath("/blog/" + post.getSlug());
		entry.setPublishAt(post.getPublishAt());
		return entry;
	}

}
