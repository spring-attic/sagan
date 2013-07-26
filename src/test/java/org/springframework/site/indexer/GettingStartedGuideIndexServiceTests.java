package org.springframework.site.indexer;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.guides.GettingStartedService;
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
	private GettingStartedGuide guide;

	@Before
	public void setUp() throws Exception {
		guide = new GettingStartedGuide("gs-awesome-guide", "awesome-guide", "Title :: Description", "Content", "Sidebar");
		ArrayList<GettingStartedGuide> guides = new ArrayList<>();
		guides.add(guide);
		given(this.gettingStartedService.listGuides()).willReturn(guides);
		this.service = new GettingStartedGuideIndexer(this.searchService, this.gettingStartedService);
	}

	@Test
	public void savesGuidesToSearchIndex() throws Exception {
		given(this.gettingStartedService.loadGuide(anyString())).willReturn(this.guide);
		this.service.indexItem(this.guide);
		verify(this.searchService).saveToIndex(any(SearchEntry.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void skipsGuidesNotFound() throws Exception {
		given(this.gettingStartedService.loadGuide(anyString())).willThrow(RestClientException.class);
		try {
			this.service.indexItem(guide);
			fail();
		} catch (Exception e) {
			verifyZeroInteractions(this.searchService);
		}
	}
}
