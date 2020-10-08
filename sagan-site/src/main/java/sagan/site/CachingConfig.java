package sagan.site;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import sagan.site.events.EventsCalendarService;
import sagan.site.guides.GettingStartedGuides;
import sagan.site.guides.Topicals;
import sagan.site.guides.Tutorials;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration
@EnableCaching
public class CachingConfig {

	@Bean
	@Profile("cloud")
	public RedisCacheManagerBuilderCustomizer myRedisCacheManagerBuilderCustomizer(SiteProperties properties) {
		SiteProperties.Cache cacheProperties = properties.getCache();
		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

		RedisCacheConfiguration eventsCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(cacheProperties.getEventsTimeToLive())
				.serializeValuesWith(fromSerializer(serializer));

		RedisCacheConfiguration guidesCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(cacheProperties.getGuidesTimeToLive())
				.serializeValuesWith(fromSerializer(serializer));

		RedisCacheConfiguration guideCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(cacheProperties.getGuideTimeToLive())
				.serializeValuesWith(fromSerializer(serializer));

		return (builder) -> builder
				.withCacheConfiguration(EventsCalendarService.CACHE_EVENTS, eventsCacheConfig)
				.withCacheConfiguration(GettingStartedGuides.CACHE_GUIDE, guideCacheConfig)
				.withCacheConfiguration(Tutorials.CACHE_TUTORIAL, guideCacheConfig)
				.withCacheConfiguration(Topicals.CACHE_TOPICAL, guideCacheConfig)
				.withCacheConfiguration(GettingStartedGuides.CACHE_GUIDES, guidesCacheConfig)
				.withCacheConfiguration(Tutorials.CACHE_TUTORIALS, guidesCacheConfig)
				.withCacheConfiguration(Topicals.CACHE_TOPICALS, guidesCacheConfig);
	}

	@Bean
	@Profile("standalone")
	public CacheManager simpleCacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(
				Stream.of(EventsCalendarService.CACHE_EVENTS,
						GettingStartedGuides.CACHE_GUIDE, GettingStartedGuides.CACHE_GUIDES,
						Tutorials.CACHE_TUTORIAL, Tutorials.CACHE_TUTORIALS,
						Topicals.CACHE_TOPICAL, Topicals.CACHE_TOPICALS)
						.map(ConcurrentMapCache::new)
						.collect(Collectors.toList()));
		return cacheManager;
	}

}
