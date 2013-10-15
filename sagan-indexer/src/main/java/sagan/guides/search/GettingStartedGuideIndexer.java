package sagan.guides.search;

import sagan.guides.service.GuidesService;
import sagan.util.index.Indexer;
import sagan.guides.Guide;
import sagan.search.service.SearchService;

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
