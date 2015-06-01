package sagan.support.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A CacheManager implementation that uses redis as a distributed cache.
 * One can configure specific cache regions in order to use them with:
 * <ul>
 *     <li>use a custom expiration value</li>
 *     <li>use a custom RedisTemplate (with configured Serializers)</li>
 * </ul>
 * When dealing with a cache region with no custom configuration, this CacheManager is using a default
 * {@link org.springframework.data.redis.core.RedisTemplate}.
 */
public class RedisCacheManager extends AbstractCacheManager {

    @SuppressWarnings("rawtypes")//
    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisTemplate defaultTemplate;
    private final Map<String, RedisTemplate> templates;

    private boolean usePrefix = true;
    private RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix();
    private boolean dynamic = false;

    // 0 - never expire
    private long defaultExpiration = 0;
    private final Map<String, Long> expires;

    public RedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.defaultTemplate = new RedisTemplate();
        this.defaultTemplate.setConnectionFactory(this.redisConnectionFactory);
        this.defaultTemplate.afterPropertiesSet();
        this.templates = new ConcurrentHashMap<String, RedisTemplate>();
        this.expires = new ConcurrentHashMap<String, Long>();
    }

    public void setUsePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    public void setCachePrefix(RedisCachePrefix cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public void setDefaultExpiration(long defaultExpiration) {
        this.defaultExpiration = defaultExpiration;
    }

    public RedisCacheManager withCache(String cacheName, long expiration) {
        return withCache(cacheName, this.defaultTemplate, expiration);
    }

    public RedisCacheManager withCache(String cacheName, RedisTemplate template, long expiration) {
        this.templates.put(cacheName, template);
        this.expires.put(cacheName, expiration);
        RedisCache cache = createCache(cacheName, template, expiration);
        addCache(cache);
        return this;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = super.getCache(name);
        if (cache == null && this.dynamic) {
            return createCache(name, this.defaultTemplate, this.defaultExpiration);
        }
        return cache;
    }

    protected RedisCache createCache(String cacheName, RedisTemplate template, long expiration) {
        return new RedisCache(cacheName, (usePrefix ? cachePrefix.prefix(cacheName) : null), template, expiration);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Assert.notNull(this.defaultTemplate, "A redis template is required in order to interact with data store");
        return this.getCacheNames().stream().map(name -> getCache(name)).collect(Collectors.toList());
    }

}
