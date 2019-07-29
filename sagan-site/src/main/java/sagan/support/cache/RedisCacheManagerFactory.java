package sagan.support.cache;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/**
 * A factory for CacheManager implementations that uses redis as a distributed cache. One
 * can configure specific cache regions in order to use them with:
 * <ul>
 * <li>use a custom expiration value</li>
 * <li>use a custom JSON type</li>
 * </ul>
 * When dealing with a cache region with no custom configuration, this CacheManager is
 * using a default serializer.
 */
public class RedisCacheManagerFactory {

    private boolean usePrefix = true;

    private final RedisConnectionFactory connectionFactory;

    private Map<String, RedisCacheConfiguration> expires;

    public RedisCacheManagerFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setUsePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    public RedisCacheManager build() {
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
                RedisCacheConfiguration.defaultCacheConfig(), this.expires);
    }

    public RedisCacheManagerFactory withCache(String cacheName, long expiration) {
        return withCache(cacheName, null, expiration);
    }

    public RedisCacheManagerFactory withCache(String cacheName, Class<?> type, long expiration) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        if (expiration > 0) {
            config = config.entryTtl(Duration.ofMillis(expiration));
        }
        if (usePrefix) {
            config = config.computePrefixWith(CacheKeyPrefix.simple());
        }
        if (type != null) {
            SerializationPair<?> serializer = SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(type));
            config = config.serializeValuesWith(serializer);
        }
        this.expires.put(cacheName, config);
        return this;
    }

}
