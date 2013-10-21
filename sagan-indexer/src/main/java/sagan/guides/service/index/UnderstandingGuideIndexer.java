package sagan.guides.service.index;

import sagan.util.index.Indexer;
import sagan.guides.UnderstandingGuide;
import sagan.guides.service.UnderstandingGuidesService;
import sagan.search.SearchEntry;
import sagan.search.service.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
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
        return guidesService.getGuides();
    }

    @Override
    public void indexItem(UnderstandingGuide indexable) {
        SearchEntry entry = new UnderstandingGuideMapper().map(indexable);
        searchService.saveToIndex(entry);
    }

    @Override
    public String counterName() {
        return "understanding";
    }

    @Override
    public String getId(UnderstandingGuide indexable) {
        return "understanding " + indexable.getSubject();
    }
}
