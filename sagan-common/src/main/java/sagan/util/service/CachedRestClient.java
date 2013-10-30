package sagan.util.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

@Component
public class CachedRestClient {

    @Cacheable(value = "cache.network", key = "#url")
    public <T> T get(RestOperations operations, String url, Class<T> clazz) {
        return operations.getForObject(url, clazz);
    }

    public <T> T post(RestOperations operations, String url, Class<T> clazz, String body) {
        HttpEntity<String> requestEntity = new HttpEntity<>(body);
        return operations.postForObject(url, requestEntity, clazz);
    }

}
