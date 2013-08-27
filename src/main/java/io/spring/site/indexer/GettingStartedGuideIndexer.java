package io.spring.site.indexer;

import io.spring.site.domain.guides.Guide;
import io.spring.site.domain.guides.GuidesService;
import io.spring.site.indexer.mapper.GuideSearchEntryMapper;
import io.spring.site.search.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
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
	public void indexItem(Guide guide) {
		searchService.saveToIndex(mapper.map(guide));
	}

	@Override
	public String counterName() {
		return "getting_started_guides";
	}

	@Override
	public String getId(Guide indexable) {
		return indexable.getGuideId();
	}
}
