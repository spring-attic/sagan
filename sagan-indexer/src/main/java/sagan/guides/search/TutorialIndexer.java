package sagan.guides.search;

import sagan.guides.Guide;
import sagan.guides.service.GuidesService;
import sagan.search.service.SearchService;
import sagan.util.index.Indexer;

import org.springframework.beans.factory.annotation.Autowired;
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
