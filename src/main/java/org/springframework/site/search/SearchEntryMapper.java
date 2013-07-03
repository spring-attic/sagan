package org.springframework.site.search;

public interface SearchEntryMapper<T> {

	SearchEntry map(T input);
}
