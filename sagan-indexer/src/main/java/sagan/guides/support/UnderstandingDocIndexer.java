package sagan.guides.support;

import sagan.Indexer;
import sagan.guides.UnderstandingDoc;
import sagan.search.support.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnderstandingDocIndexer implements Indexer<UnderstandingDoc> {

    private final SearchService searchService;
    private final UnderstandingDocs guidesService;
    private final UnderstandingDocMapper mapper = new UnderstandingDocMapper();

    @Autowired
    public UnderstandingDocIndexer(SearchService searchService, UnderstandingDocs guidesService) {
        this.searchService = searchService;
        this.guidesService = guidesService;
    }

    @Override
    public Iterable<UnderstandingDoc> indexableItems() {
        return guidesService.findAll();
    }

    @Override
    public void indexItem(UnderstandingDoc doc) {
        searchService.saveToIndex(mapper.map(doc));
    }

    @Override
    public String counterName() {
        return "understanding";
    }

    @Override
    public String getId(UnderstandingDoc indexable) {
        return "understanding " + indexable.getSubject();
    }

}
