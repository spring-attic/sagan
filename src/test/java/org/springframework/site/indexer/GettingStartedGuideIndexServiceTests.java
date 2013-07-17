package org.springframework.site.indexer;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.guides.GettingStartedService;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class GettingStartedGuideIndexServiceTests {

	private SearchService searchService = mock(SearchService.class);
	private GettingStartedService gettingStartedService = mock(GettingStartedService.class);
	private GettingStartedGuideIndexer service;
	private final GuideRepo guideRepo = new GuideRepo();

	@Before
	public void setUp() throws Exception {
		ArrayList<GuideRepo> repos = new ArrayList<GuideRepo>();
		guideRepo.setName("gs-awesome-guide");
		repos.add(guideRepo);
		given(this.gettingStartedService.listGuides()).willReturn(repos);
		this.service = new GettingStartedGuideIndexer(this.searchService, this.gettingStartedService);
	}

	@Test
	public void savesGuidesToSearchIndex() throws Exception {
		GettingStartedGuide guide = new GettingStartedGuide("awesome-guide",
				"some content", "some sidebar");
		given(this.gettingStartedService.loadGuide(anyString())).willReturn(guide);
		this.service.indexItem(guideRepo);
		verify(this.searchService).saveToIndex(any(SearchEntry.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void skipsGuidesNotFound() throws Exception {
		given(this.gettingStartedService.loadGuide(anyString())).willThrow(RestClientException.class);
		try {
			this.service.indexItem(guideRepo);
			fail();
		} catch (Exception e) {
			verifyZeroInteractions(this.searchService);
		}
	}
}
