package sagan;

import sagan.site.guides.GettingStartedGuides;
import sagan.site.guides.Topicals;
import sagan.site.guides.Tutorials;
import sagan.support.cache.CachedRestClient;
import sagan.support.cache.RedisCacheManagerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;

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

		RedisCacheManagerFactory cacheManager = new RedisCacheManagerFactory(redisConnectionFactory);

		// Use the default redisTemplate for caching REST calls
		cacheManager.withCache(CachedRestClient.CACHE_NAME, this.cacheNetworkTimeToLive);

		cacheManager.withCache(GettingStartedGuides.CACHE_GUIDE, GettingStartedGuides.CACHE_GUIDE_TYPE,
				properties.getCache().getContentTimeToLive());

		cacheManager.withCache(GettingStartedGuides.CACHE_GUIDES, GettingStartedGuides.CACHE_GUIDES_TYPE,
				properties.getCache().getListTimeToLive());

		cacheManager.withCache(Tutorials.CACHE_TUTORIAL, Tutorials.CACHE_TUTORIAL_TYPE,
				properties.getCache().getContentTimeToLive());

		cacheManager.withCache(Tutorials.CACHE_TUTORIALS, Tutorials.CACHE_TUTORIALS_TYPE,
				properties.getCache().getListTimeToLive());

		cacheManager.withCache(Topicals.CACHE_TOPICAL, Topicals.CACHE_TOPICAL_TYPE,
				properties.getCache().getContentTimeToLive());

		cacheManager.withCache(Topicals.CACHE_TOPICALS, Topicals.CACHE_TOPICALS_TYPE,
				properties.getCache().getListTimeToLive());

		return cacheManager.build();
	}

}
