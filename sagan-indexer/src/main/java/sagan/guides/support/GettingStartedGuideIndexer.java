package sagan.guides.support;

import sagan.Indexer;
import sagan.guides.GettingStartedGuide;
import sagan.search.support.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GettingStartedGuideIndexer implements Indexer<GettingStartedGuide> {

    private GuideSearchEntryMapper mapper = new GuideSearchEntryMapper();

    private final SearchService searchService;
    private final GettingStartedGuides gsGuides;

    @Autowired
    public GettingStartedGuideIndexer(SearchService searchService, GettingStartedGuides gsGuides) {
        this.searchService = searchService;
        this.gsGuides = gsGuides;
    }

    @Override
    public Iterable<GettingStartedGuide> indexableItems() {
        return gsGuides.findAll();
    }

    @Override
    public void indexItem(GettingStartedGuide guide) {
        searchService.saveToIndex(mapper.map(guide));
    }

    @Override
    public String counterName() {
        return "getting_started_guides";
    }

    @Override
    public String getId(GettingStartedGuide indexable) {
        return indexable.getGuideId();
    }
}
