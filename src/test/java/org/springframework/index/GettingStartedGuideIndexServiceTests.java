package org.springframework.index;

import org.junit.Before;
import org.junit.Test;
import org.springframework.bootstrap.actuate.metrics.CounterService;
import org.springframework.index.GettingStartedGuideIndexService;
import org.springframework.site.guides.GettingStartedGuide;
import org.springframework.site.guides.GettingStartedService;
import org.springframework.site.guides.GuideRepo;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class GettingStartedGuideIndexServiceTests {

	private SearchService searchService = mock(SearchService.class);
	private GettingStartedService gettingStartedService = mock(GettingStartedService.class);
	private GettingStartedGuideIndexService service;

	@Before
	public void setUp() throws Exception {
		ArrayList<GuideRepo> repos = new ArrayList<GuideRepo>();
		GuideRepo repo = new GuideRepo();
		repo.setName("gs-awesome-guide");
		repos.add(repo);
		when(gettingStartedService.listGuides()).thenReturn(repos);
		service = new GettingStartedGuideIndexService(searchService, gettingStartedService, mock(CounterService.class));
	}

	@Test
	public void savesGuidesToSearchIndex() throws Exception {
		GettingStartedGuide guide = new GettingStartedGuide("awesome-guide", "some content", "some sidebar");
		when(gettingStartedService.loadGuide(anyString())).thenReturn(guide);
		service.indexGuides();
		verify(searchService).saveToIndex(any(SearchEntry.class));
	}

	@Test
	public void skipsGuidesNotFound() throws Exception {
		when(gettingStartedService.loadGuide(anyString())).thenThrow(RestClientException.class);
		service.indexGuides();
		verifyZeroInteractions(searchService);
	}
}
