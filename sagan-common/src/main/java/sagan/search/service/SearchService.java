package sagan.search.service;

import sagan.search.types.SearchEntry;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface SearchService {

    void saveToIndex(SearchEntry entry);

    SearchResults search(String term, Pageable pageable, List<String> filter);
    
    void removeFromIndex(SearchEntry entry);
    
    void removeOldProjectEntriesFromIndex(String projectId, List<String> supportedVersions);
    
}
