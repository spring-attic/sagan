package sagan.support.cache;

public interface CacheService {
    String getEtagForPath(String path);

    String getContentForPath(String path);

    void cacheContent(String path, String etag, String content);
}
