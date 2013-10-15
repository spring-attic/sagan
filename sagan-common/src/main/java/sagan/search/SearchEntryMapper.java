package sagan.search;

public interface SearchEntryMapper<T> {

    SearchEntry map(T input);
}
