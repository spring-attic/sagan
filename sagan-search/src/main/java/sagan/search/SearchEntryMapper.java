package sagan.search;

import sagan.search.types.SearchEntry;

public interface SearchEntryMapper<T> {

    <R extends SearchEntry> R map(T item);

}
