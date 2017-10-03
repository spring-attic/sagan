package sagan.support.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

@Component
public class CachedRestClient {

    public static final String CACHE_NAME = "cache.network";
    public static final String CACHE_TTL_KEY = "cache.network.timetolive";
    public static final String CACHE_TTL = "${cache.network.timetolive:300}";

    @Cacheable(value = CACHE_NAME, key = "#url")
    public <T> T get(RestOperations operations, String url, Class<T> clazz) {
        return operations.getForObject(url, clazz);
    }

    /**
     * Given that this operation is needed to fetch both content and headers, caching is NOT used.
     * 
     * @param operations
     * @param url
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> ResponseEntity<T> getEntity(RestOperations operations, String url, Class<T> clazz) {
        return operations.getForEntity(url, clazz);
    }

    public <T> T post(RestOperations operations, String url, Class<T> clazz, String body) {
        HttpEntity<String> requestEntity = new HttpEntity<>(body);
        return operations.postForObject(url, requestEntity, clazz);
    }

}
