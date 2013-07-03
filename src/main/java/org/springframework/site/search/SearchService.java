package org.springframework.site.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.site.blog.Post;
import org.springframework.site.guides.GettingStartedGuide;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SearchService {

	private ElasticsearchOperations elasticsearchOperations;

	@Autowired
	public SearchService(ElasticsearchOperations elasticsearchOperations) {
		this.elasticsearchOperations = elasticsearchOperations;
	}

	// TODO: Fix potential cycle here between post and search
	public void savePostToSearchIndex(Post post) {
		if (post.isLiveOn(new Date())) {
			saveToIndex(mapPostToSearchEntry(post));
		}
	}

	public void saveGuideToSearchIndex(GettingStartedGuide guide) {
		saveToIndex(mapGuideToSearchEntry(guide));
	}

	private void saveToIndex(SearchEntry entry) {
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId(entry.getId());
		indexQuery.setObject(entry);
		elasticsearchOperations.index(indexQuery);
		elasticsearchOperations.refresh(SearchEntry.class, false);
	}

	private SearchEntry mapPostToSearchEntry(Post post) {
		SearchEntry entry = new SearchEntry();
		entry.setId(post.getId().toString());
		entry.setTitle(post.getTitle());
		entry.setSummary(post.getRenderedSummary());
		entry.setRawContent(post.getRawContent());
		entry.setPath("/blog/" + post.getSlug());
		entry.setPublishAt(post.getPublishAt());
		return entry;
	}

	private SearchEntry mapGuideToSearchEntry(GettingStartedGuide guide) {
		SearchEntry entry = new SearchEntry();
		entry.setId(guide.getGuideId());
		entry.setTitle(guide.getGuideId());
		// TODO: summary should be generated after a search matches, not statically here (so this is purely a hack for now)
		entry.setSummary(guide.getContent().substring(0, Math.min(500, guide.getContent().length())));
		entry.setRawContent(guide.getContent());
		entry.setPath("/guides/gs/" + guide.getGuideId() + "/content");
		// TODO: Can we get a publish date form github?
		entry.setPublishAt(new Date(0L));
		return entry;
	}
}
