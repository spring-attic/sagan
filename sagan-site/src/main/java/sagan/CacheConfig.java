package sagan;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import sagan.site.guides.GettingStartedGuides;
import sagan.site.guides.Topicals;
import sagan.site.guides.Tutorials;
import sagan.support.cache.CachedRestClient;
import sagan.support.cache.JsonRedisTemplate;
import sagan.support.cache.RedisCacheManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching(proxyTargetClass = true)
@Profile(SaganProfiles.STANDALONE)
class StandaloneCacheConfig {

	@Bean
	public CacheManager simpleCacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(
				Arrays.asList(CachedRestClient.CACHE_NAME,
						GettingStartedGuides.CACHE_GUIDE, GettingStartedGuides.CACHE_GUIDES,
						Tutorials.CACHE_TUTORIAL, Tutorials.CACHE_TUTORIALS,
						Topicals.CACHE_TOPICAL, Topicals.CACHE_TOPICALS).stream()
						.map(name -> new ConcurrentMapCache(name))
						.collect(Collectors.toList()));
		return cacheManager;
	}

}

@Configuration
@EnableCaching(proxyTargetClass = true)
@Profile(SaganProfiles.CLOUDFOUNDRY)
class CloudFoundryCacheConfig extends AbstractCloudConfig {

	@Value(CachedRestClient.CACHE_TTL)
	protected Long cacheNetworkTimeToLive;

	@Bean
	public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
			ObjectMapper objectMapper, SiteProperties properties) {

		RedisCacheManager cacheManager = new RedisCacheManager(redisConnectionFactory);

		// Use the default redisTemplate for caching REST calls
		cacheManager.withCache(CachedRestClient.CACHE_NAME, this.cacheNetworkTimeToLive);

		JsonRedisTemplate guideTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper,
				GettingStartedGuides.CACHE_GUIDE_TYPE);
		cacheManager.withCache(GettingStartedGuides.CACHE_GUIDE, guideTemplate,
				properties.getCache().getContentTimeToLive());

		JsonRedisTemplate guidesTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper,
				GettingStartedGuides.CACHE_GUIDES_TYPE);
		cacheManager.withCache(GettingStartedGuides.CACHE_GUIDES, guidesTemplate,
				properties.getCache().getListTimeToLive());

		JsonRedisTemplate tutorialTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper,
				Tutorials.CACHE_TUTORIAL_TYPE);
		cacheManager.withCache(Tutorials.CACHE_TUTORIAL, tutorialTemplate,
				properties.getCache().getContentTimeToLive());

		JsonRedisTemplate tutorialsTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper,
				Tutorials.CACHE_TUTORIALS_TYPE);
		cacheManager.withCache(Tutorials.CACHE_TUTORIALS, tutorialsTemplate,
				properties.getCache().getListTimeToLive());

		JsonRedisTemplate topicalTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper,
				Topicals.CACHE_TOPICAL_TYPE);
		cacheManager.withCache(Topicals.CACHE_TOPICAL, topicalTemplate,
				properties.getCache().getContentTimeToLive());

		JsonRedisTemplate topicalsTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper,
				Topicals.CACHE_TOPICALS_TYPE);
		cacheManager.withCache(Topicals.CACHE_TOPICALS, topicalsTemplate,
				properties.getCache().getListTimeToLive());

		return cacheManager;
	}

	

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return connectionFactory().redisConnectionFactory();
	}

}
