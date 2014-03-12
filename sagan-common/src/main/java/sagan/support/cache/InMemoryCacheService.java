package sagan.support.cache;

import java.util.HashMap;

import org.springframework.stereotype.Service;

@Service
public class InMemoryCacheService implements CacheService {
    private HashMap<String, String> etagCache = new HashMap<>();
    private HashMap<String, String> contentCache = new HashMap<>();

    @Override
    public String getEtagForPath(String path) {
        return etagCache.get(path);
    }

    @Override
    public String getContentForPath(String path) {
        return contentCache.get(path);
    }

    @Override
    public void cacheContent(String path, String etag, String content) {
        etagCache.put(path, etag);
        contentCache.put(path, content);
    }
}
