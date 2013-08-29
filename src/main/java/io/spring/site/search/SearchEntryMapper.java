package io.spring.site.search;

public interface SearchEntryMapper<T> {

	SearchEntry map(T input);
}
