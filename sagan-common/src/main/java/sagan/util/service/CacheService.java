package sagan.util.service;

public interface CacheService {
    String getEtagForPath(String path);

    String getContentForPath(String path);

    void cacheContent(String path, String etag, String content);
}
