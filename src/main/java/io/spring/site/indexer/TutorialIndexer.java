package io.spring.site.indexer;

import io.spring.site.domain.guides.Guide;
import io.spring.site.domain.guides.GuidesService;
import io.spring.site.indexer.mapper.TutorialMapper;
import io.spring.site.search.SearchService;

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
