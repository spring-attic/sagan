package org.springframework.site.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.site.blog.Post;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SearchService {

	private ElasticsearchOperations elasticsearchOperations;

	@Autowired
	public SearchService(ElasticsearchOperations elasticsearchOperations) {
		this.elasticsearchOperations = elasticsearchOperations;
	}

	public void savePostToSearchIndex(Post post) {
		if (post.isLiveOn(new Date())) {
			IndexQuery indexQuery = new IndexQuery();
			indexQuery.setId(post.getId().toString());

			SearchEntry searchResult = new SearchEntry();
			searchResult.setTitle(post.getTitle());
			searchResult.setSummary(post.getRenderedSummary());
			searchResult.setRawContent(post.getRawContent());
			searchResult.setPath("/blog/" + post.getSlug());
			searchResult.setPublishAt(post.getPublishAt());
			indexQuery.setObject(searchResult);
			elasticsearchOperations.index(indexQuery);
			elasticsearchOperations.refresh(SearchEntry.class, true);
		}
	}
}
