package org.springframework.search;

public interface SearchEntryMapper<T> {

	SearchEntry map(T input);
}
