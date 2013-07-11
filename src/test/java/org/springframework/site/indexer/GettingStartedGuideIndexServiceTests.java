package org.springframework.site.indexer;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.springframework.actuate.metrics.CounterService;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.guides.GettingStartedService;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.web.client.RestClientException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

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
		given(this.gettingStartedService.listGuides()).willReturn(repos);
		this.service = new GettingStartedGuideIndexService(this.searchService,
				this.gettingStartedService, mock(CounterService.class));
	}

	@Test
	public void savesGuidesToSearchIndex() throws Exception {
		GettingStartedGuide guide = new GettingStartedGuide("awesome-guide",
				"some content", "some sidebar");
		given(this.gettingStartedService.loadGuide(anyString())).willReturn(guide);
		this.service.indexGuides();
		verify(this.searchService).saveToIndex(any(SearchEntry.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void skipsGuidesNotFound() throws Exception {
		given(this.gettingStartedService.loadGuide(anyString())).willThrow(
				RestClientException.class);
		this.service.indexGuides();
		verifyZeroInteractions(this.searchService);
	}
}
