package org.springframework.site.domain.services;

public interface CacheService {
	String getEtagForPath(String path);

	String getContentForPath(String path);

	void cacheContent(String path, String etag, String content);
}
