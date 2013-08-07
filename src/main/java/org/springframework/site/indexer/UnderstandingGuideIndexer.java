package org.springframework.site.indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.understanding.UnderstandingGuide;
import org.springframework.site.domain.understanding.UnderstandingGuidesService;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;

@Service
public class UnderstandingGuideIndexer implements Indexer<UnderstandingGuide> {
	private final SearchService searchService;
	private final UnderstandingGuidesService guidesService;

	@Autowired
	public UnderstandingGuideIndexer(SearchService searchService, UnderstandingGuidesService guidesService) {
		this.searchService = searchService;
		this.guidesService = guidesService;
	}

	@Override
	public Iterable<UnderstandingGuide> indexableItems() {
		return guidesService.getGuidesList();
	}

	@Override
	public void indexItem(UnderstandingGuide indexable) {
	}

	@Override
	public String counterName() {
		return null;
	}

	@Override
	public String getId(UnderstandingGuide indexable) {
		return null;
	}
}
