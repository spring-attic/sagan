package org.springframework.site.indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.guides.Guide;
import org.springframework.site.domain.guides.GuidesService;
import org.springframework.site.indexer.mapper.TutorialMapper;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;

@Service
public class TutorialIndexer implements Indexer<Guide> {

	private TutorialMapper mapper = new TutorialMapper();

	private final SearchService searchService;
	private final GuidesService guidesService;

	@Autowired
	public TutorialIndexer(SearchService searchService, GuidesService guidesService) {
		this.searchService = searchService;
		this.guidesService = guidesService;
	}

	@Override
	public Iterable<Guide> indexableItems() {
		return guidesService.listTutorials();
	}

	@Override
	public void indexItem(Guide tutorial) {
		searchService.saveToIndex(mapper.map(tutorial));
	}

	@Override
	public String counterName() {
		return "tutorials";
	}

	@Override
	public String getId(Guide indexable) {
		return indexable.getGuideId();
	}
}
