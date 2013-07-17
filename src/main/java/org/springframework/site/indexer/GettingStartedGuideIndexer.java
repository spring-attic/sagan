package org.springframework.site.indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.guides.GettingStartedService;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.guides.GuideSearchEntryMapper;
import org.springframework.stereotype.Service;

@Service
public class GettingStartedGuideIndexer implements Indexer<GuideRepo> {

	private GuideSearchEntryMapper mapper = new GuideSearchEntryMapper();

	private final SearchService searchService;
	private final GettingStartedService gettingStartedService;

	@Autowired
	public GettingStartedGuideIndexer(SearchService searchService, GettingStartedService gettingStartedService) {
		this.searchService = searchService;
		this.gettingStartedService = gettingStartedService;
	}

	@Override
	public Iterable<GuideRepo> indexableItems() {
		return gettingStartedService.listGuides();
	}

	@Override
	public void indexItem(GuideRepo repo) {
		GettingStartedGuide guide = gettingStartedService.loadGuide(repo.getGuideId());
		searchService.saveToIndex(mapper.map(guide));
	}

	@Override
	public String counterName() {
		return "guides";
	}

	@Override
	public String getId(GuideRepo indexable) {
		return indexable.getGuideId();
	}
}
