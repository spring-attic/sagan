package org.springframework.site.search;

import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostBuilder;
import org.springframework.site.guides.GettingStartedGuide;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class SearchServiceTests {

	private ElasticsearchOperations elasticsearch = mock(ElasticsearchOperations.class);
	private SearchService searchService = new SearchService(elasticsearch);

	@Test
	public void savesPostsWhichArePublished() throws Exception {
		Post published = PostBuilder.post().id(123L).build();
		searchService.savePostToSearchIndex(published);
		verify(elasticsearch).index(any(IndexQuery.class));
	}

	@Test
	public void doesNotSavePostsWhichAreDrafts() throws Exception {
		Post draftPost = PostBuilder.post().id(123L).draft().build();
		searchService.savePostToSearchIndex(draftPost);
		verifyZeroInteractions(elasticsearch);
	}

	@Test
	public void doesNotSavePostsWhichAreScheduled() throws Exception {
		Post draftPost = PostBuilder.post().id(123L).scheduled().build();
		searchService.savePostToSearchIndex(draftPost);
		verifyZeroInteractions(elasticsearch);
	}

	@Test
	public void saveAGettingStartedGuide() {
		GettingStartedGuide guide = new GettingStartedGuide("rest-service", "This is a rest service guide", "Some sidebar stuff");
		searchService.saveGuideToSearchIndex(guide);
		verify(elasticsearch).index(any(IndexQuery.class));
	}
}
