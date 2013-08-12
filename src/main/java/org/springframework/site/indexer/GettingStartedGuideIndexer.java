package org.springframework.site.indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.guides.Guide;
import org.springframework.site.domain.guides.GuidesService;
import org.springframework.site.indexer.mapper.GuideSearchEntryMapper;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;

@Service
public class GettingStartedGuideIndexer implements Indexer<Guide> {

	private GuideSearchEntryMapper mapper = new GuideSearchEntryMapper();

	private final SearchService searchService;
	private final GuidesService guidesService;

	@Autowired
	public GettingStartedGuideIndexer(SearchService searchService, GuidesService guidesService) {
		this.searchService = searchService;
		this.guidesService = guidesService;
	}

	@Override
	public Iterable<Guide> indexableItems() {
		return guidesService.listGettingStartedGuides();
	}

	@Override
	public void indexItem(Guide guideShell) {
		Guide guide = guidesService.loadGettingStartedGuide(guideShell.getGuideId());
		searchService.saveToIndex(mapper.map(guide));
	}

	@Override
	public String counterName() {
		return "guides";
	}

	@Override
	public String getId(Guide indexable) {
		return indexable.getGuideId();
	}
}
