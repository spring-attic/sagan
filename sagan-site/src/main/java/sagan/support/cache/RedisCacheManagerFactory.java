package sagan.support.cache;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

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

    private Map<String, RedisCacheConfiguration> expires = new HashMap<>();

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
            RedisSerializer<Object> jdkSerializer = RedisSerializer.java();
            byte[] prefix = (cacheName + ":").getBytes();
            SerializationPair<String> serializer = SerializationPair.fromSerializer(new RedisSerializer<String>() {

                @Override
                public byte[] serialize(String t) throws SerializationException {
                    byte[] bytes = jdkSerializer.serialize(t);
                    byte[] result = Arrays.copyOf(prefix, prefix.length + bytes.length);
                    System.arraycopy(bytes, 0, result, prefix.length, bytes.length);
                    return result;
                }

                @Override
                public String deserialize(byte[] bytes) throws SerializationException {
                    byte[] key = new byte[bytes.length - prefix.length];
                    System.arraycopy(bytes, prefix.length, key, 0, key.length);
                    return (String) jdkSerializer.deserialize(key);
                }
            });
            config = config.computePrefixWith(name -> "");
            config = config.serializeKeysWith(serializer);
        }
        if (type != null) {
            SerializationPair<?> serializer = SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(type));
            config = config.serializeValuesWith(serializer);
        }
        this.expires.put(cacheName, config);
        return this;
    }

}
